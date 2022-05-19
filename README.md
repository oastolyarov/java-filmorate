# java-filmorate
Приложение для оценки фильмов и рекомендаций.

Ссылка на ER-диаграмму https://github.com/oastolyarov/java-filmorate/blob/main/ER-diagram.pdf

### Пример запроса за базы данных:
1. Выгрузка всех пользователей:
```
SELECT *
FROM user
```

2. Выгрузка всех фильмов:
```
SELECT *
FROM film
```

3. Выгрузка количества лайков у фильма от большего к меньшему:
```
SELECT f.id AS film_id
       COUNT(l.user_id) as likes
FROM film f
JOIN like l ON l.film_id = f.id
GROUP BY film_id
ORDER BY likes DESC
```
