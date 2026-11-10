
import { useEffect, useState } from "react";

const API = import.meta.env.VITE_API_BASE_URL || "http://localhost:8082/api/auth";

async function apiRequest(path, options = {}) {
  const response = await fetch(`${API}${path}`, {
    ...options,
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
      ...options.headers,
    },
  });

  const data = await response.json().catch(() => ({}));

  if (!response.ok) {
    throw new Error(data.message || "Request failed");
  }

  return data;
}

export default function App() {
  const [page, setPage] = useState("login");
  const [user, setUser] = useState(null);
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  // Restore the login state after refreshing the page.
  useEffect(() => {
    apiRequest("/me")
      .then((currentUser) => {
        setUser(currentUser);
        setPage("dashboard");
      })
      .catch(() => {
        setUser(null);
        setPage("login");
      });
  }, []);

  async function handleSubmit(event) {
    event.preventDefault();
    setMessage("");
    setLoading(true);

    const form = new FormData(event.currentTarget);
    const payload = Object.fromEntries(form.entries());

    try {
      if (page === "register") {
        await apiRequest("/register", {
          method: "POST",
          body: JSON.stringify(payload),
        });

        setMessage("Account created. Please log in.");
        setPage("login");
      } else {
        const result = await apiRequest("/login", {
          method: "POST",
          body: JSON.stringify(payload),
        });

        setUser(result.user);
        setPage("dashboard");
      }
    } catch (error) {
      setMessage(error.message);
    } finally {
      setLoading(false);
    }
  }

  async function handleLogout() {
    try {
      await apiRequest("/logout", {
        method: "POST",
      });

      setUser(null);
      setPage("login");
      setMessage("You have logged out.");
    } catch (error) {
      setMessage(error.message);
    }
  }

  if (page === "dashboard" && user) {
    return (
      <main className="container">
        <section className="card">
          <h1>Dashboard</h1>
          <p>Welcome, {user.name}!</p>
          <p>Email: {user.email}</p>

          <button onClick={handleLogout}>
            Logout
          </button>
        </section>
      </main>
    );
  }

  const isRegister = page === "register";

  return (
    <main className="container">
      <section className="card">
        <h1>{isRegister ? "Create Account" : "Login"}</h1>

        {message && <p className="message">{message}</p>}

        <form onSubmit={handleSubmit}>
          {isRegister && (
            <input
              name="name"
              placeholder="Full name"
              required
            />
          )}

          <input
            name="email"
            type="email"
            placeholder="Email address"
            required
          />

          <input
            name="password"
            type="password"
            placeholder="Password (minimum 8 characters)"
            minLength={isRegister ? 8 : undefined}
            required
          />

          <button type="submit" disabled={loading}>
            {loading
              ? "Please wait..."
              : isRegister
                ? "Register"
                : "Login"}
          </button>
        </form>

        <p className="switch">
          {isRegister
            ? "Already have an account?"
            : "Don't have an account?"}

          <button
            className="link"
            onClick={() => {
              setPage(isRegister ? "login" : "register");
              setMessage("");
            }}
          >
            {isRegister ? "Login" : "Register"}
          </button>
        </p>
      </section>
    </main>
  );
}
