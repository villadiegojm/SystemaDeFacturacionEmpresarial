
CREATE TABLE IF NOT EXISTS clientes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    cedula INTEGER NOT NULL,
    telefono INTEGER,
    estado TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS articulos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    codigo INTEGER NOT NULL,
    nombre TEXT NOT NULL,
    precio REAL NOT NULL,
    descripcion TEXT NOT NULL,
    cantidadstok INTEGER NOT NULL
);

CREATE TABLE facturas (
	factura_numero INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
	cliente_id INTEGER NOT NULL,
	total REAL,
	fecha TEXT DEFAULT (CURRENT_TIMESTAMP),
	FOREIGN KEY(cliente_id) REFERENCES clientes(cliente_id)
);

CREATE TABLE items (
	item_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
	factura_id INTEGER NOT NULL,
	articulo_id INTEGER NOT NULL,
	cantidad INTEGER DEFAULT 1,
	subtotal REAL NOT NULL,
	FOREIGN KEY (factura_id) REFERENCES facturas (factura_numero),
	FOREIGN KEY (articulo_id) REFERENCES articulos (codigo)
);