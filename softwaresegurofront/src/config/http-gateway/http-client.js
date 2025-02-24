import axios from "axios";

const SERVER_URL = import.meta.env.VITE_APP_SERVER_URL;
export const AxiosClient = axios.create({
    baseURL: SERVER_URL,
    withCredentials: false,
})

const requestHandler = (request) => {
    request.headers["Accept"] = "application/json";
    request.headers["Content-Type"] = "application/json";

    const session = JSON.parse(localStorage.getItem("user") || null);
    if (session?.token)
        request.headers["Authorization"] = `Bearer ${session.token}`;
        return request;
}

AxiosClient.interceptors.request.use(
    (request) => requestHandler(request),
    (error) => Promise.reject(error)
)

AxiosClient.interceptors.response.use(
    (response) => Promise.resolve(response.data),
    (error) => Promise.reject(error)
)

export default AxiosClient;