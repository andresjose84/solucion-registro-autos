interface AuthCardProps {
  title: string;
  subtitle: string;
  children: React.ReactNode;
}

export function AuthCard({ title, subtitle, children }: AuthCardProps) {
  return (
    <div className="flex min-h-screen items-center justify-center px-4 py-12">
      <div className="w-full max-w-md rounded-2xl border border-border bg-surface-card/90 p-8 shadow-2xl shadow-black/30">
        <div className="mb-8 text-center">
          <p className="font-display text-3xl text-brand-300">AutoRegistro</p>
          <h1 className="mt-3 text-xl font-semibold">{title}</h1>
          <p className="mt-2 text-sm text-slate-400">{subtitle}</p>
        </div>
        {children}
      </div>
    </div>
  );
}

interface FieldProps {
  label: string;
  error?: string;
  children: React.ReactNode;
}

export function Field({ label, error, children }: FieldProps) {
  return (
    <label className="block space-y-1.5">
      <span className="text-sm font-medium text-slate-300">{label}</span>
      {children}
      {error && <p className="text-xs text-red-400">{error}</p>}
    </label>
  );
}

export function inputClass(hasError?: boolean) {
  return [
    'w-full rounded-xl border bg-surface px-4 py-3 text-sm outline-none transition',
    'placeholder:text-slate-500 focus:border-brand-500 focus:ring-2 focus:ring-brand-500/20',
    hasError ? 'border-red-500/70' : 'border-border',
  ].join(' ');
}

export function buttonPrimaryClass(disabled?: boolean) {
  return [
    'w-full rounded-xl bg-brand-600 px-4 py-3 text-sm font-semibold text-white transition',
    'hover:bg-brand-500 focus:outline-none focus:ring-2 focus:ring-brand-400/40',
    disabled ? 'cursor-not-allowed opacity-60' : '',
  ].join(' ');
}
