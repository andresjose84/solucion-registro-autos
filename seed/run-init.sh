#!/bin/bash
set -euo pipefail

DB_HOST="${DB_HOST:-db}"
DB_PORT="${DB_PORT:-1433}"
SA_PASSWORD="${SA_PASSWORD:?SA_PASSWORD is required}"
DB_NAME="${DB_NAME:-registro_autos}"
SCRIPTS_DIR="${SCRIPTS_DIR:-/scripts}"

echo "Waiting for SQL Server at ${DB_HOST}:${DB_PORT}..."

for i in $(seq 1 60); do
    if /opt/mssql-tools18/bin/sqlcmd -S "${DB_HOST},${DB_PORT}" -U sa -P "${SA_PASSWORD}" -Q "SELECT 1" -C -b > /dev/null 2>&1; then
        echo "SQL Server is ready."
        break
    fi
    if [ "$i" -eq 60 ]; then
        echo "SQL Server did not become ready in time."
        exit 1
    fi
    sleep 2
done

echo "Running database initialization scripts..."

for script in "${SCRIPTS_DIR}"/init/*.sql; do
    echo "Executing $(basename "${script}")..."
    /opt/mssql-tools18/bin/sqlcmd -S "${DB_HOST},${DB_PORT}" -U sa -P "${SA_PASSWORD}" -i "${script}" -C -b
done

echo "Database initialization completed successfully."
