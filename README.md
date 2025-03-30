# Промежуточное ТЗ 12 Спринта
![ERD.png](ERD.png)

# Примеры запросов

## Топ N наиболее популярных фильмов
```sql
SELECT Films.id, Films.name, COUNT(Likes.user_id) AS likes_count
FROM Films
LEFT JOIN Likes ON Films.id = Likes.film_id
GROUP BY Films.id
ORDER BY likes_count DESC
LIMIT N;
```
Где N - необходимое количество популярных фильмов

## Список общих друзей с другим пользователем:
```sql
SELECT u2.id, u2.name
FROM Friends AS f1
JOIN Friends AS f2 ON f1.friend_id = f2.friend_id
JOIN Users AS u2 ON f2.friend_id = u2.id
WHERE f1.user_id = user1_id AND f2.user_id = user2_id;
```

## Получение рейтинга фильма:
```sql
SELECT Films.id, Films.name, MPA.name AS mpa_rating
FROM Films
JOIN MPA ON Films.mpa_id = MPA.id;
```

# Небольшое пояснение
Таблица Film_Genres нужна для реализации связи "многие ко многим" между фильмами (Films) и жанрами (Genres). Это позволяет одному фильму принадлежать к нескольким жанрам, а одному жанру — включать в себя множество фильмов.
