DO $$
BEGIN
  IF to_regclass('categories') IS NOT NULL THEN
    ALTER TABLE categories ADD COLUMN IF NOT EXISTS user_id bigint;
    UPDATE categories AS c
    SET user_id = u.id
    FROM (SELECT id FROM users ORDER BY id LIMIT 1) AS u
    WHERE c.user_id IS NULL AND EXISTS (SELECT 1 FROM users);
    DELETE FROM categories WHERE user_id IS NULL;
    IF EXISTS (
      SELECT 1 FROM information_schema.columns
      WHERE table_schema = current_schema()
        AND table_name = 'categories'
        AND column_name = 'user_id'
        AND is_nullable = 'YES'
    ) THEN
      ALTER TABLE categories ALTER COLUMN user_id SET NOT NULL;
    END IF;
  END IF;

  IF to_regclass('products') IS NOT NULL THEN
    ALTER TABLE products ADD COLUMN IF NOT EXISTS active boolean;
    UPDATE products SET active = true WHERE active IS NULL;
    IF EXISTS (
      SELECT 1 FROM information_schema.columns
      WHERE table_schema = current_schema()
        AND table_name = 'products'
        AND column_name = 'active'
        AND is_nullable = 'YES'
    ) THEN
      ALTER TABLE products ALTER COLUMN active SET NOT NULL;
    END IF;

    ALTER TABLE products ADD COLUMN IF NOT EXISTS category_id bigint;
    UPDATE products AS p
    SET category_id = c.id
    FROM (SELECT id FROM categories ORDER BY id LIMIT 1) AS c
    WHERE p.category_id IS NULL AND EXISTS (SELECT 1 FROM categories);
    DELETE FROM products WHERE category_id IS NULL;
    IF EXISTS (
      SELECT 1 FROM information_schema.columns
      WHERE table_schema = current_schema()
        AND table_name = 'products'
        AND column_name = 'category_id'
        AND is_nullable = 'YES'
    ) THEN
      ALTER TABLE products ALTER COLUMN category_id SET NOT NULL;
    END IF;
  END IF;
END $$;
