const PLATE_PATTERN = /^[A-Z]{3}\d{3}$/;

export function validatePlate(plate: string): string | null {
  const normalized = plate.trim().toUpperCase();
  if (!normalized) return 'La placa es obligatoria';
  if (!PLATE_PATTERN.test(normalized)) {
    return 'Formato colombiano: 3 letras + 3 numeros (ej. MWK737)';
  }
  return null;
}

export function validateYear(year: number | ''): string | null {
  if (year === '') return 'El ano es obligatorio';
  const currentYear = new Date().getFullYear();
  if (year < 1900 || year > currentYear) {
    return `El ano debe estar entre 1900 y ${currentYear}`;
  }
  return null;
}

export function validatePassword(password: string): string | null {
  if (password.length < 8) return 'Minimo 8 caracteres';
  if (!/[A-Z]/.test(password)) return 'Incluye al menos una mayuscula';
  if (!/\d/.test(password)) return 'Incluye al menos un numero';
  return null;
}

export function normalizePlate(plate: string): string {
  return plate.trim().toUpperCase();
}
