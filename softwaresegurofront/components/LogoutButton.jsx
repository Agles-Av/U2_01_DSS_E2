import React, { useContext } from "react";
import { useNavigate } from "react-router-dom"
import AuthContext from "../src/config/context/auth-context";
import { Button } from "react-bootstrap";

const LogoutButton = () => {
  const navigate = useNavigate();
  const { dispatch } = useContext(AuthContext);

  // Función para cerrar sesión
  const handleLogout = () => {
    // Eliminar los datos del usuario del localStorage
    localStorage.removeItem("user");
    localStorage.removeItem("roleUser");
    localStorage.removeItem("correouser");

    // Despachar una acción para actualizar el estado de autenticación
    dispatch({ type: "SIGNOUT" });

    // Redirigir al usuario a la página de inicio de sesión
    navigate("/");
  };

  return (

    <Button variant="danger" onClick={handleLogout}>
        Cerrar sesión
    </Button>    

  );
};

export default LogoutButton;