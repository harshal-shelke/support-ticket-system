import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import { FiClock, FiCheckCircle } from "react-icons/fi";

function StatusBadge({ status }) {
  if (status === "OPEN") {
    return (
      <span className="flex items-center gap-1 text-xs font-semibold
        text-yellow-300 bg-yellow-900/40 px-3 py-1 rounded-full">
        <FiClock size={12} /> OPEN
      </span>
    );
  }

  if (status === "CLOSED") {
    return (
      <span className="flex items-center gap-1 text-xs font-semibold
        text-green-300 bg-green-900/40 px-3 py-1 rounded-full">
        <FiCheckCircle size={12} /> CLOSED
      </span>
    );
  }

  return (
    <span className="text-xs font-semibold text-gray-300 bg-gray-700 px-3 py-1 rounded-full">
      {status}
    </span>
  );
}

function MyTickets() {
  const [tickets, setTickets] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    api
      .get("/tickets/my")
      .then((res) => setTickets(res.data))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return (
      <p className="p-6 text-gray-400 bg-[#0f172a] min-h-screen">
        Loading tickets...
      </p>
    );
  }

  return (
    <div className="min-h-screen bg-[#0f172a] py-10 px-4">
      <div className="max-w-5xl mx-auto">
        <h2 className="text-2xl font-bold mb-8 text-gray-100">
          My Tickets
        </h2>

        {tickets.length === 0 && (
          <p className="text-gray-400">
            You haven’t created any tickets yet.
          </p>
        )}

        <div className="space-y-6">
          {tickets.map((t) => (
            <div
              key={t.id}
              onClick={() => navigate(`/tickets/${t.id}`)}
              className="cursor-pointer rounded-xl border border-white/10
                         bg-[#111827] p-6
                         hover:border-blue-500/40
                         hover:shadow-[0_0_20px_-5px_rgba(59,130,246,0.4)]
                         transition-all duration-200"
            >
              <div className="flex justify-between gap-6">
                <div className="space-y-3">
                  <h3 className="text-lg font-semibold text-gray-100">
                    {t.title}
                  </h3>

                  <p className="text-sm text-gray-400 line-clamp-2">
                    {t.description}
                  </p>

                  <div className="flex items-center gap-3 pt-2">
                    <StatusBadge status={t.status} />

                    <span className="text-xs font-semibold
                      text-blue-300 bg-blue-900/40 px-3 py-1 rounded-full">
                      Priority: {t.priority}
                    </span>
                  </div>
                </div>

                <span className="text-xs text-gray-500 whitespace-nowrap">
                  {new Date(t.createdAt).toLocaleDateString()}
                </span>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

export default MyTickets;
