import type { Car } from '../types';

interface CarCardProps {
  car: Car;
  onEdit: (car: Car) => void;
  onDelete: (car: Car) => void;
}

export function CarCard({ car, onEdit, onDelete }: CarCardProps) {
  const imageUrl = car.photoUrl || 'https://placehold.co/400x250/1a222d/94a3b8?text=Sin+foto';

  return (
    <article className="group overflow-hidden rounded-2xl border border-border bg-surface-card transition hover:border-brand-600/50 hover:shadow-lg hover:shadow-brand-900/20">
      <div className="aspect-[16/10] overflow-hidden bg-surface-muted">
        <img
          src={imageUrl}
          alt={`${car.brand} ${car.model}`}
          className="h-full w-full object-cover transition duration-500 group-hover:scale-105"
          loading="lazy"
        />
      </div>
      <div className="space-y-3 p-5">
        <div className="flex items-start justify-between gap-3">
          <div>
            <h3 className="text-lg font-semibold">{car.brand} {car.model}</h3>
            <p className="text-sm text-slate-400">{car.year} · {car.color}</p>
          </div>
          <span className="rounded-lg bg-brand-900/60 px-2.5 py-1 font-mono text-sm text-brand-200">
            {car.plate}
          </span>
        </div>
        <div className="flex gap-2">
          <button
            type="button"
            onClick={() => onEdit(car)}
            className="flex-1 rounded-lg border border-border px-3 py-2 text-sm transition hover:border-brand-500 hover:text-brand-300"
          >
            Editar
          </button>
          <button
            type="button"
            onClick={() => onDelete(car)}
            className="flex-1 rounded-lg border border-red-900/60 px-3 py-2 text-sm text-red-300 transition hover:border-red-500 hover:bg-red-950/30"
          >
            Eliminar
          </button>
        </div>
      </div>
    </article>
  );
}
