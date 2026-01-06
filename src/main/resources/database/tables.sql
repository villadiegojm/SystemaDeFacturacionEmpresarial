
CREATE TABLE IF NOT EXISTS clientes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(150) NOT NULL,
    cedula INT NOT NULL,
    telefono VARCHAR(150),
    estado VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS articulos (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    codigo INT NOT NULL,
    nombre varchar(150) NOT NULL,
    precio float NOT NULL,
    descripcion TEXT NOT NULL,
    cantidadstok INT NOT NULL
);

CREATE TABLE facturas (
	factura_numero INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
	cliente_id INT NOT NULL,
	total float,
	fecha datetime DEFAULT (CURRENT_TIMESTAMP),
	FOREIGN KEY(cliente_id) REFERENCES clientes(id)
);

CREATE TABLE items (
	item_id INTEGER PRIMARY KEY AUTO_INCREMENT NOT NULL,
	factura_id INT NOT NULL,
	articulo_id INT NOT NULL,
	cantidad INT DEFAULT 1,
	subtotal float NOT NULL,
	FOREIGN KEY (factura_id) REFERENCES facturas (factura_numero),
	FOREIGN KEY (articulo_id) REFERENCES articulos (id)
);

ALTER TABLE articulos ADD COLUMN porcentaje_descuento INT DEFAULT 0;
ALTER TABLE items ADD COLUMN precio FLOAT DEFAULT 0.0;
ALTER TABLE items ADD COLUMN descuento FLOAT DEFAULT 0.0 AFTER subtotal;