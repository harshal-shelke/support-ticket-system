import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import api from "../api/axios";
import toast from "react-hot-toast";

function Login() {
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const res = await api.post("/auth/login", { email, password });
      localStorage.setItem("token", res.data.token);

      toast.success("Welcome back 👋");
      navigate("/tickets");
    } catch (err) {
      toast.error(err.response?.data?.message || "Login failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center
                    bg-[#0b1220] px-4">

      <div className="w-full max-w-md
                      bg-[#111827]
                      border border-white/10
                      rounded-2xl
                      shadow-xl
                      p-8 space-y-6">

        {/* Header */}
        <div className="text-center space-y-1">
          <h1 className="text-2xl font-semibold text-gray-100">
            Welcome Back
          </h1>
          <p className="text-sm text-gray-400">
            Sign in to continue to SupportDesk
          </p>
        </div>

        {/* Form */}
        <form onSubmit={handleLogin} className="space-y-5">

          {/* Email */}
          <div className="space-y-1">
            <label className="text-sm text-gray-300">
              Email
            </label>
            <input
              type="email"
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full rounded-lg
                         bg-[#1f2937]
                         border border-white/10
                         px-4 py-2.5
                         text-gray-200
                         placeholder-gray-500
                         focus:outline-none
                         focus:ring-2 focus:ring-blue-600"
              placeholder="you@example.com"
            />
          </div>

          {/* Password */}
          <div className="space-y-1">
            <label className="text-sm text-gray-300">
              Password
            </label>
            <input
              type="password"
              required
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full rounded-lg
                         bg-[#1f2937]
                         border border-white/10
                         px-4 py-2.5
                         text-gray-200
                         placeholder-gray-500
                         focus:outline-none
                         focus:ring-2 focus:ring-blue-600"
              placeholder="••••••••"
            />
          </div>

          {/* Button */}
          <button
            type="submit"
            disabled={loading}
            className="w-full rounded-lg
                       bg-blue-600 hover:bg-blue-700
                       transition
                       py-2.5
                       font-medium
                       text-white
                       disabled:opacity-50"
          >
            {loading ? "Signing in..." : "Sign In"}
          </button>
        </form>

        {/* Footer */}
        <p className="text-sm text-center text-gray-400">
          New here?{" "}
          <Link
            to="/register"
            className="text-blue-400 hover:text-blue-300"
          >
            Create an account
          </Link>
        </p>

      </div>
    </div>
  );
}

export default Login;
