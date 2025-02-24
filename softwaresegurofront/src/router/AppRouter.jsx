import React, { useContext, useEffect } from 'react'
import { Route, RouterProvider, createBrowserRouter, createRoutesFromElements } from 'react-router-dom'
import AuthContext from '../config/context/auth-context'
import AdminLayout from '../modules/admin/AdminLayout'
import Error403 from '../modules/errors/Error403'
import Login from '../modules/login/Login'

const AppRouter = () => {
    const { user } = useContext(AuthContext);
    const [role, setRole] = React.useState(user?.roleUser || localStorage.getItem("roleUser"));

    useEffect(() => {
        setRole(user?.roleUser || localStorage.getItem("roleUser"));
    }, [user]);

    const paths = (role) => {
        switch (role) {
            case "admin":
                return <Route path='/admin' element={<AdminLayout />} />
            default:
                return <Route path='*' element={<Error403 />} />
        }
    }
    const router = createBrowserRouter(
        createRoutesFromElements(
            <>
            {/** Ruta para el login */}
            <Route path='/' element={<Login/>} />
            {/** Ruta para el error 403 */}
            {user?.signed ? paths(role) : <Route path='*' element={<Error403 />} /> }
            </>
        )
    );
    return (
        <div>AppRouter</div>
    )
}

export default AppRouter