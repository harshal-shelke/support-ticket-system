import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../api/axios";
import toast from "react-hot-toast";
import { getUserRole } from "../utils/auth";

function TicketDetails() {
  const { id } = useParams();
  const role = getUserRole();

  const [ticket, setTicket] = useState(null);
  const [remark, setRemark] = useState("");
  const [status, setStatus] = useState("");
  const [staffEmail, setStaffEmail] = useState("");
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    api.get(`/tickets/${id}`)
      .then((res) => {
        setTicket(res.data);
        setStatus(res.data.status);
      })
      .catch((err) =>
        toast.error(err.response?.data?.message || "Failed to load ticket")
      )
      .finally(() => setLoading(false));
  }, [id]);

  const updateStatus = async () => {
    try {
      const res = await api.patch(
        `/tickets/status/${id}?status=${status}`
      );
      setTicket(res.data);
      toast.success("Status updated");
    } catch {
      toast.error("Status update failed");
    }
  };

  const addRemark = async () => {
    if (!remark.trim()) return toast.error("Remark cannot be empty");

    setSubmitting(true);
    try {
      const res = await api.post(`/tickets/remarks/${id}`, {
        message: remark,
      });
      setTicket({ ...ticket, remarks: res.data });
      setRemark("");
      toast.success("Remark added");
    } catch {
      toast.error("Failed to add remark");
    } finally {
      setSubmitting(false);
    }
  };

  const assignTicket = async () => {
    if (!staffEmail.trim()) return toast.error("Enter staff email");

    try {
      const res = await api.patch(
        `/admin/tickets/assign/${id}?staffEmail=${staffEmail}`
      );
      setTicket(res.data);
      setStaffEmail("");
      toast.success("Ticket assigned");
    } catch {
      toast.error("Assign failed");
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-[#0b1220]
                      flex items-center justify-center text-gray-400">
        Loading ticket...
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#0b1220] px-6 py-10">
      <div className="max-w-4xl mx-auto space-y-6">

        {/* Ticket Header */}
        <div className="bg-[#111827] border border-white/10
                        rounded-xl p-6 space-y-4">
          <h1 className="text-2xl font-semibold text-gray-100">
            {ticket.title}
          </h1>

          <p className="text-gray-300">
            {ticket.description}
          </p>

          <div className="flex gap-6 text-sm pt-2">
            <span className="text-gray-400">
              Status:
              <b className="ml-1 text-blue-400">{ticket.status}</b>
            </span>
            <span className="text-gray-400">
              Priority:
              <b className="ml-1 text-yellow-400">{ticket.priority}</b>
            </span>
            <span className="text-gray-400">
              Assigned:
              <b className="ml-1 text-indigo-400">
                {ticket.assignedTo || "Unassigned"}
              </b>
            </span>
          </div>
        </div>

        {/* Status Update */}
        {(role === "STAFF" || role === "ADMIN") && (
          <div className="bg-[#111827] border border-white/10
                          rounded-xl p-6 flex gap-4 items-center">
            <select
              value={status}
              onChange={(e) => setStatus(e.target.value)}
              className="bg-[#1f2937] border border-white/10
                         rounded-md p-2 text-gray-200"
            >
              <option>OPEN</option>
              <option>IN_PROGRESS</option>
              <option>RESOLVED</option>
              <option>CLOSED</option>
            </select>

            <button
              onClick={updateStatus}
              className="bg-green-600 hover:bg-green-700
                         px-4 py-2 rounded-md text-white"
            >
              Update Status
            </button>
          </div>
        )}

        {/* Admin Assign */}
        {role === "ADMIN" && (
          <div className="bg-[#111827] border border-white/10
                          rounded-xl p-6 flex gap-4">
            <input
              value={staffEmail}
              onChange={(e) => setStaffEmail(e.target.value)}
              placeholder="staff@email.com"
              className="flex-1 bg-[#1f2937]
                         border border-white/10 rounded-md
                         px-3 py-2 text-gray-200"
            />
            <button
              onClick={assignTicket}
              className="bg-blue-600 hover:bg-blue-700
                         px-4 py-2 rounded-md text-white"
            >
              Assign
            </button>
          </div>
        )}

        {/* Remarks */}
        <div className="bg-[#111827] border border-white/10
                        rounded-xl p-6 space-y-4">
          <h2 className="font-semibold text-gray-100">
            Remarks
          </h2>

          {ticket.remarks?.length === 0 && (
            <p className="text-sm text-gray-400">
              No remarks yet.
            </p>
          )}

          <div className="space-y-3">
            {ticket.remarks?.map((r, i) => (
              <div
                key={i}
                className="bg-[#1f2937] border border-white/10
                           rounded-lg p-3"
              >
                <p className="text-sm text-gray-200">
                  {r.message}
                </p>
                <span className="text-xs text-gray-500">
                  {r.createdBy} ·{" "}
                  {new Date(r.createdAt).toLocaleString()}
                </span>
              </div>
            ))}
          </div>

          <textarea
            rows={3}
            value={remark}
            onChange={(e) => setRemark(e.target.value)}
            placeholder="Add a remark..."
            className="w-full bg-[#1f2937] border border-white/10
                       rounded-md p-3 text-gray-200"
          />

          <button
            onClick={addRemark}
            disabled={submitting}
            className="bg-blue-600 hover:bg-blue-700
                       px-4 py-2 rounded-md text-white"
          >
            {submitting ? "Adding..." : "Add Remark"}
          </button>
        </div>

      </div>
    </div>
  );
}

export default TicketDetails;
