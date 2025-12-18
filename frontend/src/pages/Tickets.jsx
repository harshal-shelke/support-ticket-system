import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import toast from "react-hot-toast";
import { getUserRole } from "../utils/auth";

function Tickets() {
  const navigate = useNavigate();
  const role = getUserRole();

  const [tickets, setTickets] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let endpoint = "";

    if (role === "CUSTOMER") endpoint = "/tickets/my";
    else if (role === "STAFF") endpoint = "/tickets/assigned";
    else if (role === "ADMIN") endpoint = "/admin/tickets";
    else {
      toast.error("Invalid role");
      return;
    }

    api.get(endpoint)
      .then((res) => setTickets(res.data))
      .catch((err) =>
        toast.error(err.response?.data?.message || "Failed to load tickets")
      )
      .finally(() => setLoading(false));
  }, [role]);

  if (loading) {
    return (
      <div className="min-h-screen bg-[#0b1220]
                      flex items-center justify-center text-gray-400">
        Loading tickets...
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#0b1220] px-6 py-8">

      {/* Header */}
      <div className="max-w-4xl mx-auto mb-6 flex justify-between items-center">
        <h1 className="text-2xl font-semibold text-gray-100">
          {role === "CUSTOMER"
            ? "My Tickets"
            : role === "STAFF"
            ? "Assigned Tickets"
            : "All Tickets"}
        </h1>

        {role === "CUSTOMER" && (
          <button
            onClick={() => navigate("/tickets/create")}
            className="bg-blue-600 hover:bg-blue-700
                       transition px-4 py-2 rounded-lg
                       text-white text-sm"
          >
            + New Ticket
          </button>
        )}
      </div>

      {/* Empty State */}
      {tickets.length === 0 && (
        <div className="max-w-4xl mx-auto
                        bg-[#111827] border border-white/10
                        rounded-xl p-10 text-center">
          <p className="text-gray-400">
            No tickets found.
          </p>
        </div>
      )}

      {/* Ticket List */}
      <div className="max-w-4xl mx-auto space-y-5">
        {tickets.map((t) => (
          <div
            key={t.id}
            onClick={() => navigate(`/tickets/${t.id}`)}
            className="cursor-pointer
                       bg-[#111827] border border-white/10
                       rounded-xl p-6 space-y-3
                       hover:border-blue-500/50
                       transition"
          >
            <h3 className="text-lg font-medium text-gray-100">
              {t.title}
            </h3>

            <p className="text-sm text-gray-400 line-clamp-2">
              {t.description}
            </p>

            <div className="flex justify-between items-center pt-2 text-xs">
              <span className="px-2 py-1 rounded-full font-medium
                               bg-blue-500/10 text-blue-400">
                {t.status}
              </span>

              <span className="text-gray-500">
                {new Date(t.createdAt).toLocaleDateString()}
              </span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default Tickets;
