import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import toast from "react-hot-toast";

function CreateTicket() {
  const navigate = useNavigate();

  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [loading, setLoading] = useState(false);

  const handleCreate = async (e) => {
    e.preventDefault();

    if (!title.trim() || !description.trim()) {
      toast.error("All fields are required");
      return;
    }

    setLoading(true);
    try {
      await api.post("/tickets/create-ticket", {
        title,
        description,
      });

      toast.success("Ticket created successfully 🎫");
      navigate("/tickets");
    } catch (err) {
      toast.error(err.response?.data?.message || "Failed to create ticket");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center
                    bg-[#0b1220] px-4">

      <div className="w-full max-w-2xl
                      bg-[#111827]
                      border border-white/10
                      rounded-2xl
                      shadow-xl
                      p-8 space-y-6">

        {/* Header */}
        <div className="space-y-1">
          <h1 className="text-2xl font-semibold text-gray-100">
            Create Support Ticket
          </h1>
          <p className="text-sm text-gray-400">
            Describe your issue and we’ll take care of it
          </p>
        </div>

        {/* Form */}
        <form onSubmit={handleCreate} className="space-y-5">

          {/* Title */}
          <div className="space-y-1">
            <label className="text-sm text-gray-300">
              Title
            </label>
            <input
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              className="w-full rounded-lg
                         bg-[#1f2937]
                         border border-white/10
                         px-4 py-2.5
                         text-gray-200
                         focus:outline-none
                         focus:ring-2 focus:ring-blue-600"
              placeholder="Brief summary of the issue"
            />
          </div>

          {/* Description */}
          <div className="space-y-1">
            <label className="text-sm text-gray-300">
              Description
            </label>
            <textarea
              rows={5}
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              className="w-full rounded-lg
                         bg-[#1f2937]
                         border border-white/10
                         px-4 py-3
                         text-gray-200
                         resize-none
                         focus:outline-none
                         focus:ring-2 focus:ring-blue-600"
              placeholder="Explain the problem in detail..."
            />
          </div>

          {/* Button */}
          <div className="flex justify-end">
            <button
              type="submit"
              disabled={loading}
              className="rounded-lg
                         bg-blue-600 hover:bg-blue-700
                         transition
                         px-6 py-2.5
                         font-medium
                         text-white
                         disabled:opacity-50"
            >
              {loading ? "Submitting..." : "Submit Ticket"}
            </button>
          </div>
        </form>

      </div>
    </div>
  );
}

export default CreateTicket;
