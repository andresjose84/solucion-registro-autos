import { useEffect, useState } from 'react';
import type { Car, CarFormData } from '../types';
import { normalizePlate, validatePlate, validateYear } from '../utils/validators';
import { Field, inputClass } from './AuthCard';

interface CarFormModalProps {
  open: boolean;
  initial?: Car | null;
  onClose: () => void;
  onSubmit: (data: CarFormData) => Promise<void>;
}

const emptyForm: CarFormData = {
  brand: '',
  model: '',
  year: '',
  plate: '',
  color: '',
  photoUrl: '',
};

export function CarFormModal({ open, initial, onClose, onSubmit }: CarFormModalProps) {
  const [form, setForm] = useState<CarFormData>(emptyForm);
  const [errors, setErrors] = useState<Partial<Record<keyof CarFormData, string>>>({});
  const [submitting, setSubmitting] = useState(false);
  const [serverError, setServerError] = useState('');

  useEffect(() => {
    if (open) {
      setForm(initial ? {
        brand: initial.brand,
        model: initial.model,
        year: initial.year,
        plate: initial.plate,
        color: initial.color,
        photoUrl: initial.photoUrl ?? '',
      } : emptyForm);
      setErrors({});
      setServerError('');
    }
  }, [open, initial]);

  if (!open) return null;

  const validate = (): boolean => {
    const nextErrors: Partial<Record<keyof CarFormData, string>> = {};
    if (!form.brand.trim()) nextErrors.brand = 'La marca es obligatoria';
    if (!form.model.trim()) nextErrors.model = 'El modelo es obligatorio';
    if (!form.color.trim()) nextErrors.color = 'El color es obligatorio';

    const plateError = validatePlate(form.plate);
    if (plateError) nextErrors.plate = plateError;

    const yearError = validateYear(form.year);
    if (yearError) nextErrors.year = yearError;

    setErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!validate()) return;

    setSubmitting(true);
    setServerError('');
    try {
      await onSubmit({
        ...form,
        plate: normalizePlate(form.plate),
      });
      onClose();
    } catch (error) {
      setServerError(error instanceof Error ? error.message : 'No se pudo guardar el auto');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-end justify-center bg-black/70 p-4 sm:items-center">
      <div className="max-h-[90vh] w-full max-w-lg overflow-y-auto rounded-2xl border border-border bg-surface-card p-6 shadow-2xl">
        <div className="mb-6 flex items-center justify-between">
          <h2 className="text-xl font-semibold">{initial ? 'Editar auto' : 'Agregar auto'}</h2>
          <button type="button" onClick={onClose} className="text-slate-400 hover:text-white">✕</button>
        </div>

        <form className="space-y-4" onSubmit={handleSubmit}>
          <div className="grid gap-4 sm:grid-cols-2">
            <Field label="Marca" error={errors.brand}>
              <input
                className={inputClass(!!errors.brand)}
                value={form.brand}
                onChange={(e) => setForm({ ...form, brand: e.target.value })}
              />
            </Field>
            <Field label="Modelo" error={errors.model}>
              <input
                className={inputClass(!!errors.model)}
                value={form.model}
                onChange={(e) => setForm({ ...form, model: e.target.value })}
              />
            </Field>
          </div>

          <div className="grid gap-4 sm:grid-cols-2">
            <Field label="Ano" error={errors.year}>
              <input
                type="number"
                className={inputClass(!!errors.year)}
                value={form.year}
                onChange={(e) => setForm({ ...form, year: e.target.value ? Number(e.target.value) : '' })}
              />
            </Field>
            <Field label="Placa (ej. MWK737)" error={errors.plate}>
              <input
                className={inputClass(!!errors.plate)}
                value={form.plate}
                onChange={(e) => setForm({ ...form, plate: e.target.value.toUpperCase() })}
                maxLength={6}
              />
            </Field>
          </div>

          <Field label="Color" error={errors.color}>
            <input
              className={inputClass(!!errors.color)}
              value={form.color}
              onChange={(e) => setForm({ ...form, color: e.target.value })}
            />
          </Field>

          <Field label="Foto (URL simulada)">
            <input
              className={inputClass()}
              placeholder="https://..."
              value={form.photoUrl}
              onChange={(e) => setForm({ ...form, photoUrl: e.target.value })}
            />
          </Field>

          {serverError && <p className="text-sm text-red-400">{serverError}</p>}

          <div className="flex gap-3 pt-2">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 rounded-xl border border-border px-4 py-3 text-sm"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={submitting}
              className="flex-1 rounded-xl bg-brand-600 px-4 py-3 text-sm font-semibold text-white hover:bg-brand-500 disabled:opacity-60"
            >
              {submitting ? 'Guardando...' : initial ? 'Actualizar' : 'Crear'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
