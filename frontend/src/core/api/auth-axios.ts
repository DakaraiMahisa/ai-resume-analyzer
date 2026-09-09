import axios from "axios";

const authApiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
});

export default authApiClient;
