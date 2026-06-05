import { useCallback, useEffect, useMemo, useState } from 'react';
import { createCar, deleteCar, fetchCars, updateCar } from '../api/cars';
import { extractErrorMessage } from '../api/client';
import { CarCard } from '../components/CarCard';
import { CarFormModal } from '../components/CarFormModal';
import { Layout } from '../components/Layout';
import { SearchFilters } from '../components/SearchFilters';
import type { Car, CarFilters, CarFormData } from '../types';

export function CarsPage() {
  const [cars, setCars] = useState<Car[]>([]);
  const [allBrands, setAllBrands] = useState<string[]>([]);
  const [filters, setFilters] = useState<CarFilters>({ search: '', brand: '', year: '' });
  const [debouncedSearch, setDebouncedSearch] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [editingCar, setEditingCar] = useState<Car | null>(null);

  useEffect(() => {
    const timer = setTimeout(() => setDebouncedSearch(filters.search), 300);
    return () => clearTimeout(timer);
  }, [filters.search]);

  const loadCars = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const data = await fetchCars({
        search: debouncedSearch || undefined,
        brand: filters.brand || undefined,
        year: filters.year || undefined,
      });
      setCars(data);
    } catch (err) {
      setError(extractErrorMessage(err).message);
    } finally {
      setLoading(false);
    }
  }, [debouncedSearch, filters.brand, filters.year]);

  const loadBrands = useCallback(async () => {
    try {
      const data = await fetchCars();
      const brands = [...new Set(data.map((car) => car.brand))].sort();
      setAllBrands(brands);
    } catch {
      setAllBrands([]);
    }
  }, []);

  useEffect(() => {
    loadCars();
  }, [loadCars]);

  useEffect(() => {
    loadBrands();
  }, [loadBrands]);

  const brandOptions = useMemo(() => {
    const merged = new Set([...allBrands, ...cars.map((car) => car.brand)]);
    return [...merged].sort();
  }, [allBrands, cars]);

  const handleCreateOrUpdate = async (data: CarFormData) => {
    try {
      if (editingCar) {
        await updateCar(editingCar.id, data);
      } else {
        await createCar(data);
      }
      await loadCars();
      await loadBrands();
    } catch (err) {
      throw new Error(extractErrorMessage(err).message);
    }
  };

  const handleDelete = async (car: Car) => {
    const confirmed = window.confirm(`¿Eliminar ${car.brand} ${car.model} (${car.plate})?`);
    if (!confirmed) return;

    try {
      await deleteCar(car.id);
      await loadCars();
      await loadBrands();
    } catch (err) {
      setError(extractErrorMessage(err).message);
    }
  };

  return (
    <Layout
      title="Mis vehiculos"
      subtitle="Administra tu flota personal: busca, filtra y mantén actualizada la informacion de cada auto."
    >
      <div className="mb-6 flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h2 className="font-display text-3xl text-white">Garaje</h2>
          <p className="text-sm text-slate-400">{cars.length} vehiculo(s) encontrado(s)</p>
        </div>
        <button
          type="button"
          onClick={() => {
            setEditingCar(null);
            setModalOpen(true);
          }}
          className="rounded-xl bg-brand-600 px-5 py-3 text-sm font-semibold text-white transition hover:bg-brand-500"
        >
          + Agregar auto
        </button>
      </div>

      <div className="mb-6">
        <SearchFilters filters={filters} brands={brandOptions} onChange={setFilters} />
      </div>

      {error && (
        <div className="mb-4 rounded-xl border border-red-900/60 bg-red-950/20 px-4 py-3 text-sm text-red-300">
          {error}
        </div>
      )}

      {loading ? (
        <div className="grid place-items-center py-20 text-slate-400">Cargando autos...</div>
      ) : cars.length === 0 ? (
        <div className="rounded-2xl border border-dashed border-border bg-surface-card/50 px-6 py-16 text-center">
          <p className="text-lg font-medium">No hay autos para mostrar</p>
          <p className="mt-2 text-sm text-slate-400">Agrega tu primer vehiculo o ajusta los filtros.</p>
        </div>
      ) : (
        <div className="grid gap-5 sm:grid-cols-2 xl:grid-cols-3">
          {cars.map((car) => (
            <CarCard
              key={car.id}
              car={car}
              onEdit={(selected) => {
                setEditingCar(selected);
                setModalOpen(true);
              }}
              onDelete={handleDelete}
            />
          ))}
        </div>
      )}

      <CarFormModal
        open={modalOpen}
        initial={editingCar}
        onClose={() => {
          setModalOpen(false);
          setEditingCar(null);
        }}
        onSubmit={handleCreateOrUpdate}
      />
    </Layout>
  );
}
