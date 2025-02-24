import React, { useContext, useEffect } from 'react';
import { Route, RouterProvider, createBrowserRouter, createRoutesFromElements, Outlet } from 'react-router-dom';
import AuthContext from '../config/context/auth-context';
import Error403 from '../modules/errors/Error403';
import Login from '../modules/login/Login';
import AdminTable from '../modules/admin/AdminTable';
import UserCrud from '../modules/admin/UserCrud';
import Erorr404 from '../modules/errors/Erorr404';

const AppRouter = () => {
    const { user } = useContext(AuthContext);
    const [role, setRole] = React.useState(user?.roleUser || localStorage.getItem("roleUser"));

    useEffect(() => {
        setRole(user?.roleUser || localStorage.getItem("roleUser"));
    }, [user]);

    // Layout para las rutas de administrador
    const AdminLayout = () => {
        return (
            <div>
                {/* Aquí puedes agregar un menú o barra de navegación común para las rutas de admin */}
                <Outlet /> {/* Esto renderiza las rutas hijas */}
            </div>
        );
    };

    const paths = (role) => {
        switch (role) {
            case "admin":
                return (
                    <Route path='/' element={<AdminLayout />}>
                         <Route path='/' element={<AdminLayout />} />
                        <Route path='/admin' element={<UserCrud />} />
                        <Route path='/bitacora' element={<AdminTable />} /> {/* Ruta /admin/bitacora */}
                        <Route path='*' element={<Erorr404 />} /> {/* Ruta /admin/bitacora */}
                    </Route>
                );
            default:
                return (
                    <Route path='/admin' element={<Error403 />}>
                        <Route index element={<Error403/>} /> {/* Ruta por defecto (/admin) */}
                        <Route path='bitacora' element={<Error403 />} /> {/* Ruta /admin/bitacora */}
                        <Route path='*' element={<Erorr404 />} /> {/* Ruta /admin/bitacora */}
                    </Route>
                );
        }
    };

    const router = createBrowserRouter(
        createRoutesFromElements(
            <>
                {/** Ruta para el login */}
                <Route path='/' element={<Login />} />
                {/** Rutas protegidas */}
                {user?.signed ? paths(role) : <Route path='*' element={<Error403 />} />}
            </>
        )
    );

    return <RouterProvider router={router} />;
};

export default AppRouter;