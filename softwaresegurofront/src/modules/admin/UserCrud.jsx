import React, { useState, useEffect } from "react";
import axios from "axios";
import { Table, Button, Modal, Form } from "react-bootstrap";
import AxiosClient from "../../config/http-gateway/http-client";
import { useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import AdminLayout from "./AdminLayout";

const UserCrud = () => {
  const navigate = useNavigate();
  const [users, setUsers] = useState([]); // Estado para almacenar la lista de usuarios
  const [showModal, setShowModal] = useState(false); // Estado para controlar el modal
  const [selectedUser, setSelectedUser] = useState(null); // Estado para el usuario seleccionado
  const [formData, setFormData] = useState({
    nombre: "",
    apellido: "",
    correo: "",
    telefono: "",
    password: "",
    edad: "",
  });


  // Obtener todos los usuarios
  const fetchUsers = async () => {
    const emailuser = localStorage.getItem("correouser");

    try {
      const response = await AxiosClient({
        method: "GET",
        url: `/user/${emailuser}/`, // Cambia "admin" por el usuario que realiza la acción
      });
      setUsers(response.data);
    } catch (error) {
      console.error("Error fetching users:", error);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  // Manejar cambios en el formulario
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  // Abrir modal para crear o editar usuario
  const handleShowModal = (user = null) => {
    setSelectedUser(user);
    if (user) {
      setFormData({
        nombre: user.nombre,
        apellido: user.apellido,
        correo: user.correo,
        telefono: user.telefono,
        password: user.password,
        edad: user.edad,
      });
    } else {
      setFormData({
        nombre: "",
        apellido: "",
        correo: "",
        telefono: "",
        password: "",
        edad: "",
      });
    }
    setShowModal(true);
  };

  // Cerrar modal
  const handleCloseModal = () => {
    setShowModal(false);
    setSelectedUser(null);
  };

  // Crear o actualizar usuario
  const handleSubmit = async (e) => {
    e.preventDefault();
    const emailuser = localStorage.getItem("correouser");
    const dataUpdate = {
      nombre: formData.nombre,
      apellido: formData.apellido,
      correo: formData.correo,
      telefono: formData.telefono,
      edad: formData.edad,
    };
    try {
      if (selectedUser) {
        const response = await AxiosClient({
          method: "PUT",
          url: `/user/${emailuser}/${selectedUser.id}`, // Cambia "admin" por el usuario que realiza la acción
          data: dataUpdate,
        });
        console.log(response);
      } else {
        // Crear usuario
        const response = await AxiosClient({
          method: "POST",
          url: `/user/${emailuser}/`, // Cambia "admin" por el usuario que realiza la acción
          data: formData,
        });
        console.log(response);
      }
      fetchUsers(); // Actualizar la lista de usuarios
      handleCloseModal(); // Cerrar el modal
    } catch (error) {
      console.error("Error saving user:", error);
    }
  };

  const fokinAgles = async () => {
    navigate("/bitacora");
  };

  // Eliminar usuario
// Eliminar usuario
const handleDelete = async (id) => {
  const emailuser = localStorage.getItem("correouser");

  // Mostrar confirmación con SweetAlert2
  const result = await Swal.fire({
    title: "¿Estás seguro?",
    text: "¡No podrás revertir esto!",
    icon: "warning",
    showCancelButton: true,
    confirmButtonColor: "#3085d6",
    cancelButtonColor: "#d33",
    confirmButtonText: "Sí, eliminar",
    cancelButtonText: "Cancelar",
  });

  // Si el usuario confirma, eliminar el usuario
  if (result.isConfirmed) {
    try {
      const response = await AxiosClient({
        method: "DELETE",
        url: `/user/${emailuser}/${id}`,
      });
      fetchUsers(); // Actualizar la lista de usuarios
      Swal.fire("¡Eliminado!", "El usuario ha sido eliminado.", "success");
    } catch (error) {
      console.error("Error deleting user:", error);
      Swal.fire("Error", "No se pudo eliminar el usuario.", "error");
    }
  }
};

  // Filtrar usuarios para excluir el que tiene id = 1
  const filteredUsers = users.filter((user) => user.id !== 1);

  return (
    <>
    <AdminLayout/>
    <div className="container mt-5">
      <h1>CRUD de Usuarios</h1>
      <Button variant="primary" onClick={() => handleShowModal()}>
        Crear Usuario
      </Button>
      <Button variant="primary" onClick={() => fokinAgles()}>
        Ver bitácora
      </Button>
      <Table striped bordered hover className="mt-3">
        <thead>
          <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>Apellido</th>
            <th>Correo</th>
            <th>Teléfono</th>
            <th>Edad</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {filteredUsers.map((user) => (
            <tr key={user.id}>
              <td>{user.id}</td>
              <td>{user.nombre}</td>
              <td>{user.apellido}</td>
              <td>{user.correo}</td>
              <td>{user.telefono}</td>
              <td>{user.edad}</td>
              <td>
                <Button variant="warning" onClick={() => handleShowModal(user)}>
                  Editar
                </Button>{" "}
                <Button variant="danger" onClick={() => handleDelete(user.id)}>
                  Eliminar
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>

      {/* Modal para crear/editar usuario */}
      <Modal show={showModal} onHide={handleCloseModal}>
        <Modal.Header closeButton>
          <Modal.Title>
            {selectedUser ? "Editar Usuario" : "Crear Usuario"}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form onSubmit={handleSubmit}>
            <Form.Group>
              <Form.Label>Nombre</Form.Label>
              <Form.Control
                type="text"
                name="nombre"
                value={formData.nombre}
                onChange={handleInputChange}
                required
                maxLength={50}
                // No caracteres especiales
                pattern="[A-Za-z ]{1,50}"
              />
            </Form.Group>
            <Form.Group>
              <Form.Label>Apellido</Form.Label>
              <Form.Control
                type="text"
                name="apellido"
                value={formData.apellido}
                onChange={handleInputChange}
                required
                maxLength={100}
                // No caracteres especiales
                pattern="[A-Za-z ]{1,50}"
              />
            </Form.Group>
            <Form.Group>
              <Form.Label>Correo</Form.Label>
              <Form.Control
                type="email"
                name="correo"
                value={formData.correo}
                onChange={handleInputChange}
                required
                maxLength={260}
              />
            </Form.Group>
            <Form.Group>
              <Form.Label>Teléfono</Form.Label>
              <Form.Control
                type="text"
                name="telefono"
                value={formData.telefono}
                onChange={handleInputChange}
                required
                maxLength={10}
                // Solo números
                pattern="[0-9]{10}"
              />
            </Form.Group>
            {!selectedUser && (
              <Form.Group>
                <Form.Label>Contraseña</Form.Label>
                <Form.Control
                  type="password"
                  name="password"
                  value={formData.password}
                  onChange={handleInputChange}
                  required
                  maxLength={50}
                />
              </Form.Group>
            )}
            <Form.Group>
              <Form.Label>Edad</Form.Label>
              <Form.Control
                type="number"
                name="edad"
                value={formData.edad}
                required
                onChange={(e) => {
                  const value = e.target.value;
                  if (value.length <= 3 && value <= 120) {
                    handleInputChange(e);
                  }
                }}
              />
            </Form.Group>
            <Button variant="primary" type="submit" className="mt-3">
              {selectedUser ? "Actualizar" : "Crear"}
            </Button>
          </Form>
        </Modal.Body>
      </Modal>
    </div>
    </>
    
  );
};

export default UserCrud;