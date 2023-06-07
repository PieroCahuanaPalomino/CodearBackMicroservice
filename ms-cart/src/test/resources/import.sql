INSERT INTO cart (customer_id, creation_date) VALUES(333, NOW());

INSERT INTO cart_item (cart_id, product_id, name, price, quantity, sub_total, creation_date) VALUES(1, 1, 'Laptop', 800, 2, 1600, NOW());
INSERT INTO cart_item (cart_id, product_id, name, price, quantity, sub_total, creation_date) VALUES(1, 2, 'Monitor', 700, 1, 700, NOW());