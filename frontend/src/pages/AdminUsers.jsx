import { useEffect, useState } from "react";
import api from "../api/axios";
import toast from "react-hot-toast";
import { getUserRole } from "../utils/auth";

const ROLES = ["CUSTOMER", "STAFF", "ADMIN"];

function AdminUsers() {
  const role = getUserRole();
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (role !== "ADMIN") {
      toast.error("Access denied");
      return;
    }

    api.get("/admin/users")
      .then(res => setUsers(res.data))
      .catch(err =>
        toast.error(err.response?.data || "Failed to load users")
      )
      .finally(() => setLoading(false));
  }, [role]);

  const updateRole = async (email, newRole) => {
    try {
      const res = await api.patch(
        `/admin/users/change-role?email=${email}&role=${newRole}`
      );
      toast.success(res.data);
      refresh();
    } catch (err) {
      toast.error(err.response?.data || "Role update failed");
    }
  };

  const toggleUser = async (email, active) => {
    try {
      const res = await api.patch(
        active
          ? `/admin/users/disable?email=${email}`
          : `/admin/users/enable?email=${email}`
      );
      toast.success(res.data);
      refresh();
    } catch (err) {
      toast.error(err.response?.data || "Action failed");
    }
  };

  const refresh = async () => {
    const res = await api.get("/admin/users");
    setUsers(res.data);
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-[#0b1220] flex items-center justify-center text-gray-400">
        Loading users...
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#0b1220] p-6">
      <h2 className="text-xl font-semibold text-gray-100 mb-6">
        User Management
      </h2>

      <div className="space-y-4">
        {users.map(u => (
          <div
            key={u.email}
            className="bg-[#111827] border border-white/10 rounded-xl
                       p-5 flex justify-between items-center"
          >
            <div>
              <p className="text-gray-100 font-medium">{u.name}</p>
              <p className="text-sm text-gray-400">{u.email}</p>
              <p className="text-xs mt-1">
                Status:
                <span className={`ml-1 font-semibold ${
                  u.active ? "text-green-400" : "text-red-400"
                }`}>
                  {u.active ? "ACTIVE" : "DISABLED"}
                </span>
              </p>
            </div>

            <div className="flex items-center gap-4">
              <select
                value={u.role}
                onChange={(e) => updateRole(u.email, e.target.value)}
                className="bg-[#1f2937] border border-white/10
                           text-gray-200 rounded-md p-2"
              >
                {ROLES.map(r => (
                  <option key={r} value={r}>{r}</option>
                ))}
              </select>

              <button
                onClick={() => toggleUser(u.email, u.active)}
                className={`px-4 py-2 rounded-md text-white ${
                  u.active
                    ? "bg-red-600 hover:bg-red-700"
                    : "bg-green-600 hover:bg-green-700"
                }`}
              >
                {u.active ? "Disable" : "Enable"}
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default AdminUsers;
