import { Link, useNavigate } from "react-router-dom";
import { getUserRole } from "../utils/auth";
import { jwtDecode } from "jwt-decode";
import { Ticket, PlusCircle, Users, LogOut, User } from "lucide-react";

function Navbar() {
  const navigate = useNavigate();
  const role = getUserRole();

  const token = localStorage.getItem("token");

  let displayName = "User";

  if (token) {
    try {
      const decoded = jwtDecode(token);

      // ✅ CORRECT PRIORITY ORDER
      if (decoded.email) {
        displayName = decoded.email.split("@")[0];
      } else if (decoded.username) {
        displayName = decoded.username;
      } else if (decoded.sub && decoded.sub !== role) {
        // prevent role duplication
        displayName = decoded.sub;
      }
    } catch {
      displayName = "User";
    }
  }

  const logout = () => {
    localStorage.removeItem("token");
    navigate("/login");
  };

  return (
    <nav className="sticky top-0 z-50 bg-[#020617]/90 backdrop-blur border-b border-white/10">
      <div className="max-w-7xl mx-auto px-6 h-16 flex justify-between items-center">

        {/* BRAND */}
        <Link
          to="/tickets"
          className="text-white font-semibold text-lg tracking-wide"
        >
          SupportDesk
        </Link>

        {/* RIGHT SIDE */}
        <div className="flex items-center gap-4 text-sm">

          {!role && (
            <>
              <Link className="nav-link" to="/login">Login</Link>
              <Link className="nav-link" to="/register">Register</Link>
            </>
          )}

          {role && (
            <>
              <Link className="nav-link flex items-center gap-1" to="/tickets">
                <Ticket size={16} />
                My Tickets
              </Link>

              {role === "CUSTOMER" && (
                <Link
                  className="nav-link flex items-center gap-1"
                  to="/tickets/create"
                >
                  <PlusCircle size={16} />
                  New Ticket
                </Link>
              )}

              {role === "ADMIN" && (
                <Link
                  className="nav-link flex items-center gap-1"
                  to="/admin/users"
                >
                  <Users size={16} />
                  Manage Users
                </Link>
              )}

              {/* ✅ USER NAME ONLY (NO ROLE) */}
              <div
                className="flex items-center gap-2 px-3 py-1.5
                           rounded-full bg-white/5 border border-white/10
                           text-gray-200"
              >
                <User size={14} className="text-gray-400" />
                <span >{displayName}</span>
              </div>

              {/* ✅ ROLE SHOWN ONLY HERE */}
              <span
                className={`px-2 py-1 rounded-full text-xs font-semibold
                  ${
                    role === "ADMIN"
                      ? "bg-red-500/20 text-red-400"
                      : role === "STAFF"
                      ? "bg-yellow-500/20 text-yellow-400"
                      : "bg-blue-500/20 text-blue-400"
                  }
                `}
              >
                {role}
              </span>

              {/* LOGOUT */}
              <button
                onClick={logout}
                className="flex items-center gap-1 px-3 py-1.5 rounded-md
                           text-red-400 hover:text-red-300
                           hover:bg-red-500/10 transition"
              >
                <LogOut size={16} />
                Logout
              </button>
            </>
          )}

        </div>
      </div>
    </nav>
  );
}

export default Navbar;
