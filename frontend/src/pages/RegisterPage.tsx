import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { extractErrorMessage } from '../api/client';
import { AuthCard, Field, buttonPrimaryClass, inputClass } from '../components/AuthCard';
import { useAuthStore } from '../store/authStore';
import { validatePassword } from '../utils/validators';

export function RegisterPage() {
  const navigate = useNavigate();
  const register = useAuthStore((s) => s.register);
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    const passwordError = validatePassword(password);
    if (passwordError) {
      setError(passwordError);
      return;
    }

    setLoading(true);
    setError('');
    try {
      await register(fullName, email, password);
      navigate('/login');
    } catch (err) {
      setError(extractErrorMessage(err).message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthCard
      title="Crear cuenta"
      subtitle="Registra tu perfil y comienza a administrar tus autos."
    >
      <form className="space-y-4" onSubmit={handleSubmit}>
        <Field label="Nombre completo">
          <input
            className={inputClass()}
            value={fullName}
            onChange={(e) => setFullName(e.target.value)}
            required
          />
        </Field>
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
          {loading ? 'Registrando...' : 'Crear cuenta'}
        </button>
      </form>
      <p className="mt-6 text-center text-sm text-slate-400">
        ¿Ya tienes cuenta?{' '}
        <Link to="/login" className="text-brand-400 hover:text-brand-300">Inicia sesion</Link>
      </p>
    </AuthCard>
  );
}
