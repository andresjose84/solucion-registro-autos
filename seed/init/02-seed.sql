USE registro_autos;
GO

IF NOT EXISTS (SELECT 1 FROM users WHERE email = N'demo@registroautos.com')
BEGIN
    INSERT INTO users (email, password, full_name)
    VALUES (
        N'demo@registroautos.com',
        N'$2y$10$pq.MZXU91egPJChs9TMoWOuAT5g42uZ9OWNdfQl0qObE1iV0grk3W',
        N'Usuario Demo'
    );

    DECLARE @demoUserId BIGINT = SCOPE_IDENTITY();

    INSERT INTO cars (user_id, brand, model, year, plate, color, photo_url)
    VALUES
        (@demoUserId, N'Chevrolet', N'Spark GT', 2020, N'MWK737', N'Rojo', N'https://s1.cdn.autoevolution.com/images-webp/models/CHEVROLET_Spark-2018_main.jpg.webp?text=Chevrolet+Spark'),
        (@demoUserId, N'Mazda', N'CX-5', 2022, N'ABC123', N'Negro', N'https://s1.cdn.autoevolution.com/images-webp/models/MAZDA_CX-5-2025_main.jpg.webp?text=Mazda+CX-5'),
        (@demoUserId, N'Renault', N'Duster', 2019, N'XYZ456', N'Blanco', N'https://s1.cdn.autoevolution.com/images-webp/models/RENAULT_Duster-OROCH-2015_main.jpg.webp?text=Renault+Duster');
END
GO
