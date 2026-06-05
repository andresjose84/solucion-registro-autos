IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'registro_autos')
BEGIN
    CREATE DATABASE registro_autos;
END
GO

USE registro_autos;
GO

IF OBJECT_ID(N'dbo.cars', N'U') IS NOT NULL
    DROP TABLE dbo.cars;
GO

IF OBJECT_ID(N'dbo.users', N'U') IS NOT NULL
    DROP TABLE dbo.users;
GO

CREATE TABLE users (
    id          BIGINT IDENTITY(1,1) PRIMARY KEY,
    email       NVARCHAR(255) NOT NULL UNIQUE,
    password    NVARCHAR(255) NOT NULL,
    full_name   NVARCHAR(255) NOT NULL,
    created_at  DATETIME2 NOT NULL DEFAULT GETDATE()
);
GO

CREATE TABLE cars (
    id          BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id     BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    brand       NVARCHAR(100) NOT NULL,
    model       NVARCHAR(100) NOT NULL,
    year        INT NOT NULL,
    plate       NVARCHAR(10) NOT NULL,
    color       NVARCHAR(50) NOT NULL,
    photo_url   NVARCHAR(500) NULL,
    created_at  DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at  DATETIME2 NOT NULL DEFAULT GETDATE(),
    CONSTRAINT uq_user_plate UNIQUE (user_id, plate)
);
GO

CREATE INDEX idx_cars_user_id ON cars(user_id);
GO
