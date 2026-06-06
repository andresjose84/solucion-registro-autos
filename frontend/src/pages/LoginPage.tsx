import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { extractErrorMessage } from '../api/client';
import { AuthCard, Field, buttonPrimaryClass, inputClass } from '../components/AuthCard';
import { useAuthStore } from '../store/authStore';

export function LoginPage() {
  const navigate = useNavigate();
  const login = useAuthStore((s) => s.login);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    setLoading(true);
    setError('');
    try {
      await login(email, password);
      navigate('/cars');
    } catch (err) {
      setError(extractErrorMessage(err).message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthCard
      title="Iniciar sesion"
      subtitle="Accede a tu garaje digital y gestiona tus vehiculos."
    >
      <form className="space-y-4" onSubmit={handleSubmit}>
        <Field label="Email">
          <input
            type="email"
            className={inputClass()}
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </Field>
        <Field label="Contrasena">
          <input
            type="password"
            className={inputClass()}
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </Field>
        {error && <p className="text-sm text-red-400">{error}</p>}
        <button type="submit" disabled={loading} className={buttonPrimaryClass(loading)}>
          {loading ? 'Ingresando...' : 'Entrar'}
        </button>
      </form>
      <p className="mt-6 text-center text-sm text-slate-400">
        ¿No tienes cuenta?{' '}
        <Link to="/register" className="text-brand-400 hover:text-brand-300">Registrate</Link>
      </p>
    </AuthCard>
  );
}
