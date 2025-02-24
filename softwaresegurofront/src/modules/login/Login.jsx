import React, { useState, useContext } from 'react';
import AxiosClient from '../../config/http-gateway/http-client';
import { useNavigate } from 'react-router-dom';
import AuthContext from '../../config/context/auth-context';


const Login = () => {
  // Hook para redireccionar
  const navigate = useNavigate();
  // Contexto de autenticación
  const { user,dispatch } = useContext(AuthContext);
  // Estados para email y contraseña
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  // Función para manejar el envío del formulario
  const handleSubmit = async (e) => {
    e.preventDefault(); // Evita que la página se recargue
    console.log("Email:", email);
    console.log("Password:", password);
    
    const data = {
      correo: email,
      password: password,
    }
    console.log("Data:", data);
    
    try {
      const response = await AxiosClient({
        method: "POST",
        url: "/auth/signin",
        data: data,
      })
      console.log(response);

      if(response.data.user.correo === "cafatofo@gmail.com"){
        const userData = {
          token: response.data.token,
          roleUser: "admin",
          signed: true,
        }
        dispatch({ type: "SIGNIN", payload: userData });
        localStorage.setItem("user", JSON.stringify(userData));
        localStorage.setItem("roleUser", "admin");
        localStorage.setItem("correouser", JSON.stringify(email));
  
        // Redirigir a la página de administrador
        navigate("/admin");
      }
    } catch (error) {
      console.error("Error al iniciar sesión", error);
      
    }finally{
      console.log("Finalizado");
      
    }
  };

  return (
    <div className="container mt-5">
      <div className="row justify-content-center">
        <div className="col-md-6">
          <div className="card">
            <div className="card-body">
              <h1 className="card-title text-center mb-4">Iniciar Sesión</h1>
              <form onSubmit={handleSubmit}>
                {/* Campo de correo electrónico */}
                <div className="mb-3">
                  <label htmlFor="email" className="form-label">
                    Correo Electrónico
                  </label>
                  <input
                    type="email"
                    className="form-control"
                    id="email"
                    placeholder="Ingresa tu correo electrónico"
                    required
                    value={email} 
                    onChange={(e) => setEmail(e.target.value)} 
                    maxLength={50}
                  />
                </div>

                {/* Campo de contraseña */}
                <div className="mb-3">
                  <label htmlFor="password" className="form-label">
                    Contraseña
                  </label>
                  <input
                    type="password"
                    className="form-control"
                    id="password"
                    placeholder="Ingresa tu contraseña"
                    required
                    value={password} 
                    onChange={(e) => setPassword(e.target.value)} 
                  />
                </div>

                {/* Botón de envío */}
                <div className="d-grid">
                  <button type="submit" className="btn btn-primary">
                    Iniciar Sesión
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;