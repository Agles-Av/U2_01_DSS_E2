import React, { useState, useEffect } from "react";
import AxiosClient from "../../config/http-gateway/http-client";
import { Table } from "react-bootstrap";
import AdminLayout from "./AdminLayout";

const AdminTable = () => {
  const [bitacoraData, setBitacoraData] = useState([]);

  // Función para dividir el texto en fragmentos con saltos de línea
  const formatText = (text, maxLength) => {
    if (!text) return ""; // Si no hay texto, retornar vacío
    const chunks = [];
    for (let i = 0; i < text.length; i += maxLength) {
      chunks.push(text.slice(i, i + maxLength));
    }
    return chunks.join("<br />"); // Unir los fragmentos con <br />
  };

  // Obtener los datos de la bitácora
  const fetchBitacoraData = async () => {
    try {
      const response = await AxiosClient({
        method: "GET",
        url: "/bitacora/",
      });
      setBitacoraData(response.data);
    } catch (error) {
      console.error("Error fetching bitacora data:", error);
    }
  };

  useEffect(() => {
    fetchBitacoraData();
  }, []);

  return (
    <>
      <AdminLayout />
      <div className="container mt-5">
        <div className="row">
          <div className="col-md-12">
            <h1>Bitácora de Actividades</h1>
            <Table striped bordered hover>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Usuario</th>
                  <th>Acción</th>
                  <th>Tabla Afectada</th>
                  <th style={{ width: "100px", maxWidth: "100px" }}>Datos Anteriores</th>
                  <th style={{ width: "100px", maxWidth: "100px" }}>Datos Nuevos</th>
                  <th>Fecha</th>
                </tr>
              </thead>
              <tbody>
                {bitacoraData.map((registro) => (
                  <tr key={registro.id}>
                    <td>{registro.id}</td>
                    <td>{registro.usuario}</td>
                    <td>{registro.accion}</td>
                    <td>{registro.tablaAfectada}</td>
                    <td
                      style={{ width: "100px", maxWidth: "300px" }}
                      dangerouslySetInnerHTML={{
                        __html: formatText(registro.datosAnteriores, 20), // 20 caracteres por línea
                      }}
                    />
                    <td
                      style={{ width: "100px", maxWidth: "300px" }}
                      dangerouslySetInnerHTML={{
                        __html: formatText(registro.datosNuevos, 20), // 20 caracteres por línea
                      }}
                    />
                    <td>{new Date(registro.fecha).toLocaleString()}</td>
                  </tr>
                ))}
              </tbody>
            </Table>
          </div>
        </div>
      </div>
    </>
  );
};

export default AdminTable;