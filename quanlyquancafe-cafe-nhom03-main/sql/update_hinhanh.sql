-- Cập nhật hình ảnh cho các món trong bảng Mon
-- Chạy script này nếu database đã có dữ liệu nhưng chưa có hinhAnh

USE QuanLyQuanCafe;
GO

UPDATE Mon SET hinhAnh = N'Trasuatruyenthong.jpg'  WHERE maMon = 'MON001';
UPDATE Mon SET hinhAnh = N'Caphesua.jpg'            WHERE maMon = 'MON002';
UPDATE Mon SET hinhAnh = N'Matchalatte.jpg'           WHERE maMon = 'MON003';
UPDATE Mon SET hinhAnh = N'Tradaocamsa.jpg'        WHERE maMon = 'MON004';
UPDATE Mon SET hinhAnh = N'Capheden.jpg'            WHERE maMon = 'MON005';
UPDATE Mon SET hinhAnh = N'Banhmitrung.jpg'         WHERE maMon = 'MON006';
UPDATE Mon SET hinhAnh = N'Banhcroissant.jpg'         WHERE maMon = 'MON007';

PRINT N'Đã cập nhật hình ảnh cho tất cả các món!';
GO
