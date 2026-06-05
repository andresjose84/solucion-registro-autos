import type { CarFilters } from '../types';
import { inputClass } from './AuthCard';

interface SearchFiltersProps {
  filters: CarFilters;
  brands: string[];
  onChange: (filters: CarFilters) => void;
}

export function SearchFilters({ filters, brands, onChange }: SearchFiltersProps) {
  return (
    <div className="grid gap-3 rounded-2xl border border-border bg-surface-card p-4 sm:grid-cols-2 lg:grid-cols-4">
      <div className="sm:col-span-2">
        <label className="mb-1.5 block text-sm text-slate-400">Buscar por placa o modelo</label>
        <input
          className={inputClass()}
          placeholder="Ej. MWK737 o Spark"
          value={filters.search}
          onChange={(e) => onChange({ ...filters, search: e.target.value })}
        />
      </div>
      <div>
        <label className="mb-1.5 block text-sm text-slate-400">Marca</label>
        <select
          className={inputClass()}
          value={filters.brand}
          onChange={(e) => onChange({ ...filters, brand: e.target.value })}
        >
          <option value="">Todas</option>
          {brands.map((brand) => (
            <option key={brand} value={brand}>{brand}</option>
          ))}
        </select>
      </div>
      <div>
        <label className="mb-1.5 block text-sm text-slate-400">Ano</label>
        <input
          type="number"
          className={inputClass()}
          placeholder="2020"
          value={filters.year}
          onChange={(e) => onChange({ ...filters, year: e.target.value })}
        />
      </div>
    </div>
  );
}
