#!/bin/sh

# Script para copiar archivos placeholder al volumen de evidencias
# Solo copia si el volumen está vacío

UPLOAD_DIR="/app/uploads"
PLACEHOLDER_DIR="/app/placeholders"

# Crear directorio si no existe
mkdir -p "$UPLOAD_DIR"

# Si el directorio está vacío, copiar placeholders
if [ -z "$(ls -A $UPLOAD_DIR)" ]; then
    echo "📁 Copiando archivos placeholder al volumen de evidencias..."
    cp -r "$PLACEHOLDER_DIR"/* "$UPLOAD_DIR"/
    echo "✅ Archivos placeholder copiados"
else
    echo "📂 El volumen de evidencias ya contiene archivos, omitiendo copia de placeholders"
fi

# Ejecutar el comando principal del contenedor
exec "$@"
