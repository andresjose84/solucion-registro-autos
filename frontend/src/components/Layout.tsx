import type { ReactNode } from 'react';
import { useAuthStore } from '../store/authStore';

interface LayoutProps {
  children: ReactNode;
  title?: string;
  subtitle?: string;
}

export function Layout({ children, title, subtitle }: LayoutProps) {
  const user = useAuthStore((s) => s.user);
  const logout = useAuthStore((s) => s.logout);

  return (
    <div className="min-h-screen bg-[radial-gradient(circle_at_top,_#1a222d_0%,_#0f1419_55%)]">
      <header className="border-b border-border/80 bg-surface/80 backdrop-blur-md sticky top-0 z-40">
        <div className="mx-auto flex max-w-7xl items-center justify-between px-4 py-4 sm:px-6 lg:px-8">
          <div>
            <p className="font-display text-2xl text-brand-300">AutoRegistro</p>
            {title && <h1 className="text-sm text-slate-400">{title}</h1>}
          </div>
          {user && (
            <div className="flex items-center gap-3 sm:gap-4">
              <div className="hidden text-right sm:block">
                <p className="text-sm font-medium">{user.fullName}</p>
                <p className="text-xs text-slate-400">{user.email}</p>
              </div>
              <button
                type="button"
                onClick={logout}
                className="rounded-lg border border-border px-3 py-2 text-sm text-slate-300 transition hover:border-brand-500 hover:text-brand-300"
              >
                Salir
              </button>
            </div>
          )}
        </div>
      </header>
      <main className="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
        {subtitle && (
          <p className="mb-6 max-w-2xl text-slate-400">{subtitle}</p>
        )}
        {children}
      </main>
    </div>
  );
}
