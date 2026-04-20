-- Crear tabla users para el sistema JUPITER
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    all_name VARCHAR(255) NOT NULL,
    rol VARCHAR(50) NOT NULL,
    clan_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para mejor rendimiento
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_rol ON users(rol);

-- Comentario para documentación
COMMENT ON TABLE users IS 'Tabla de usuarios del sistema JUPITER';
COMMENT ON COLUMN users.id IS 'ID único autoincremental';
COMMENT ON COLUMN users.email IS 'Email único del usuario';
COMMENT ON COLUMN users.password IS 'Contraseña del usuario (en producción usar hashing)';
COMMENT ON COLUMN users.all_name IS 'Nombre completo del usuario';
COMMENT ON COLUMN users.rol IS 'Rol del usuario (ADMIN, CODER, TL)';
COMMENT ON COLUMN users.clan_id IS 'ID del clan al que pertenece (opcional)';
