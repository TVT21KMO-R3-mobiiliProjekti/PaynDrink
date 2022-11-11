-- Active: 1667640699426@@dpg-cdj2l8mn6mpngrtb5a5g-a.frankfurt-postgres.render.com@5432@tvt21kmo_r3_mobiiliprojekti
CREATE TABLE restaurant(  
    id_restaurant INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    restaurant_name VARCHAR(255),
    restaurant_address VARCHAR(255),
    restaurant_description VARCHAR(255),
    picture_url VARCHAR(255)
);
CREATE TABLE restaurant_type(
    id_restaurant_type INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    restaurant_type_name VARCHAR(45),
    picture_url VARCHAR(255),
    id_restaurant INT NOT NULL
);
CREATE TABLE waiter(
    id_waiter INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    picture_url VARCHAR(255),
    waiter_description VARCHAR(255),
    id_restaurant INT NOT NULL
);
CREATE TABLE item(
    id_item INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    item_name VARCHAR(45),
    quantity INT,
    price NUMERIC,
    quick_order INT,
    item_description VARCHAR(255),
    picture_url VARCHAR(255),
    item_type INT,
    id_restaurant INT NOT NULL
);
CREATE TABLE seating(
    id_seating INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    seat_number INT,
    seat_type INT,
    id_restaurant INT NOT NULL
);
CREATE TABLE seating_type(
    id_seating_type INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    seating_type_name VARCHAR(45),
    picture_url VARCHAR(255),
    id_seating INT NOT NULL
);
CREATE TABLE item_type(
    id_item_type INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    item_type_name VARCHAR(45),
    picture_url VARCHAR(255)
);
CREATE TABLE seating_has_items(
    id_seating_has_items INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    id_seating_type INT NOT NULL,
    id_item_type INT NOT NULL
);
CREATE TABLE item_has_types(
    id_item_has_types INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    id_item INT NOT NULL,
    id_item_type INT NOT NULL
);
CREATE TABLE orders(
    id_order INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    order_price NUMERIC,
    order_placed TIMESTAMP,
    order_fullfilled TIMESTAMP,
    refund NUMERIC,
    refund_reason VARCHAR(255),
    id_restaurant INT NOT NULL,
    id_seating INT NOT NULL,
    id_server INT NOT NULL
);
CREATE TABLE tip(
    id_tip INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    amount NUMERIC,
    id_order INT NOT NULL,
    id_server INT NOT NULL
);
CREATE TABLE order_has_item(
    id_order_has_item INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    quantity INT,
    id_order INT NOT NULL,
    id_item INT NOT NULL
);
CREATE TABLE user_login(
    id_login INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    username VARCHAR(255),
    pw VARCHAR(255),
    user_role INT,
    id_waiter INT NOT NULL,
    id_restaurant INT NOT NULL
);
COMMENT ON TABLE  IS '';
COMMENT ON COLUMN .name IS '';