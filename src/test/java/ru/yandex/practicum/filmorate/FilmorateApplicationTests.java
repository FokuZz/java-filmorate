package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.FriendsDao;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.exception.HasNoBeenFoundException;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorageImpl;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
	private final UserService userService;
	private final FilmService filmService;

	private final FilmDbStorageImpl filmDbStorage;
	private final FriendsDao friendsDao;
	private final GenreDao genreDao;
	private final MpaDao mpaDao;
	private final LocalDate testReleaseDate = LocalDate.of(2000, 1, 1);
	private final JdbcTemplate jdbcTemplate;
	User.UserBuilder userBuilder;
	Film.FilmBuilder filmBuilder;
	Genre.GenreBuilder genreBuilder;
	Mpa.MpaBuilder mpaBuilder;

	@BeforeEach
	public void setup() {
		userBuilder = User.builder()
				.email("e@mail.ru")
				.login("Login")
				.name("Name")
				.birthday(LocalDate.of(1985, 9, 7));

		mpaBuilder = Mpa.builder()
				.id(1);

		genreBuilder = Genre.builder()
				.id(1);

		filmBuilder = Film.builder()
				.name("Film name")
				.description("Film description")
				.releaseDate(testReleaseDate)
				.duration(90)
				.mpa(mpaBuilder.build());
	}

	@AfterEach
	public void jdbcClear() {
		jdbcTemplate.update("DELETE FROM USERS;");
		jdbcTemplate.update("DELETE FROM FILMS;");
		jdbcTemplate.update("DELETE FROM FRIENDS;");
		jdbcTemplate.update("DELETE FROM FILM_GENRE;");
		jdbcTemplate.update("DELETE FROM LIKES;");
		jdbcTemplate.update("ALTER TABLE USERS ALTER COLUMN user_id RESTART WITH 1;");
		jdbcTemplate.update("ALTER TABLE FILMS ALTER COLUMN film_id RESTART WITH 1;");
	}

	@Test
	public void createUser() {
		User user = userBuilder.build();
		User userAdded = userService.create(user);
		assertThat(userAdded)
				.isNotNull()
				.hasFieldOrPropertyWithValue("id", 1);
	}

	@Test
	public void getUser() {
		User user = userBuilder.build();
		User userAdded = userService.create(user);
		User userFound = userService.get(userAdded.getId());
		assertThat(userFound)
				.isNotNull()
				.hasFieldOrPropertyWithValue("id", 1)
				.isEqualTo(userAdded);

		HasNoBeenFoundException ex = assertThrows(
				HasNoBeenFoundException.class,
				() -> userService.get(-1)
		);
		assertEquals("The object has no been Found", ex.getMessage());

		ex = assertThrows(
				HasNoBeenFoundException.class,
				() -> userService.get(999)
		);
		assertEquals("The object has no been Found", ex.getMessage());
	}

	@Test
	public void getAllUsers() {
		List<User> users = userService.get();
		assertThat(users)
				.isNotNull()
				.isEqualTo(Collections.EMPTY_LIST);

		User user = userBuilder.build();
		userService.create(user);
		users = userService.get();
		assertNotNull(users);
		assertEquals(users.size(), 1);
		assertEquals(users.get(0).getId(), 1);
	}

	@Test
	public void updateUser() {
		User user = userBuilder.build();
		userService.create(user);
		User userToUpdate = userBuilder.id(1).name("Name Updated").build();
		User userUpdated = userService.update(userToUpdate);
		assertThat(userUpdated)
				.isNotNull()
				.hasFieldOrPropertyWithValue("id", 1)
				.hasFieldOrPropertyWithValue("name", "Name Updated");

		HasNoBeenFoundException ex = assertThrows(
				HasNoBeenFoundException.class,
				() -> userService.update(userBuilder.id(-1).build())
		);
		assertEquals("The object has no been Found", ex.getMessage());

		ex = assertThrows(
				HasNoBeenFoundException.class,
				() -> userService.update(userBuilder.id(999).build())
		);
		assertEquals("The object has no been Found", ex.getMessage());
	}

	@Test
	public void createFilm() {
		Film film = filmBuilder.build();
		Film filmAdded = filmService.create(film);
		assertThat(filmAdded)
				.isNotNull()
				.hasFieldOrPropertyWithValue("id", 1);
	}

	@Test
	public void getFilm() {
		Film film = filmBuilder.build();
		Film filmAdded = filmService.create(film);
		Film filmFound = filmService.get(filmAdded.getId());
		assertThat(filmFound)
				.isNotNull()
				.hasFieldOrPropertyWithValue("id", 1)
				.hasFieldOrPropertyWithValue("mpa", mpaBuilder.name("G").build());
		//.isEqualTo(filmAdded);

		HasNoBeenFoundException ex = assertThrows(
				HasNoBeenFoundException.class,
				() -> filmService.get(-1)
		);
		assertEquals("The object has no been Found", ex.getMessage());

		ex = assertThrows(
				HasNoBeenFoundException.class,
				() -> filmService.get(999)
		);
		assertEquals("The object has no been Found", ex.getMessage());
	}

	@Test
	public void getAllFilm() {
		List<Film> films = filmService.get();
		assertThat(films)
				.isNotNull()
				.isEqualTo(Collections.EMPTY_LIST);

		Film film = filmBuilder.build();
		filmService.create(film);
		films = filmService.get();
		assertNotNull(films);
		assertEquals(films.size(), 1);
		assertEquals(films.get(0).getId(), 1);
	}

	@Test
	public void updateFilm() {
		Film film = filmBuilder.build();
		filmService.create(film);
		Film filmToUpdate = filmBuilder.id(1).name("Film name Updated").build();
		Film filmUpdated = filmService.update(filmToUpdate);
		assertThat(filmUpdated)
				.isNotNull()
				.hasFieldOrPropertyWithValue("id", 1)
				.hasFieldOrPropertyWithValue("name", "Film name Updated");

		HasNoBeenFoundException ex = assertThrows(
				HasNoBeenFoundException.class,
				() -> filmService.update(filmBuilder.id(-1).build())
		);
		assertEquals("The object has no been Found", ex.getMessage());

		ex = assertThrows(
				HasNoBeenFoundException.class,
				() -> filmService.update(filmBuilder.id(999).build())
		);
		assertEquals("The object has no been Found", ex.getMessage());
	}

	@Test
	public void getTopFilms() {
		List<Film> topFilms = filmService.getTopLiked(10);
		assertThat(topFilms)
				.isNotNull()
				.isEqualTo(Collections.EMPTY_LIST);

		filmService.create(filmBuilder.build());
		filmService.create(filmBuilder.name("oneFilmss").description("bebe").build());
		userService.create(userBuilder.build());

		topFilms = filmService.getTopLiked(1);
		assertNotNull(topFilms);
		assertEquals(topFilms.size(), 1);
		assertEquals(topFilms.get(0).getId(), 1);

		filmService.addLike(2, 1);
		topFilms = filmService.getTopLiked(2);
		assertNotNull(topFilms);
		assertEquals(topFilms.size(), 2);
		assertEquals(topFilms.get(0).getId(), 2);
	}

	@Test
	public void addFilmGenre() {
		Film film = filmBuilder.build();
		filmService.create(film);
		filmDbStorage.addFilmGenre(1, 1);

		List<Genre> genreId = genreDao.getGenreByFilmId(1);
		assertNotNull(genreId);
		assertEquals(genreId.size(), 1);
		assertEquals(genreId.get(0).getId(), 1);

		filmDbStorage.addFilmGenre(1, 2);
		genreId = genreDao.getGenreByFilmId(1);
		assertNotNull(genreId);
		assertEquals(genreId.size(), 2);
		assertEquals(genreId.get(0).getId(), 1);
	}

	@Test
	public void deleteFilmGenre() {
		Film film = filmBuilder.build();
		filmService.create(film);
		filmDbStorage.addFilmGenre(1, 1);
		filmDbStorage.addFilmGenre(1, 2);

		filmDbStorage.deleteFilmGenre(1, 2);

		List<Genre> genres = genreDao.getGenreByFilmId(1);
		assertNotNull(genres);
		assertEquals(genres.size(), 1);
		assertEquals(genres.get(0).getId(), 1);

		filmDbStorage.deleteFilmGenre(1, 1);

		genres = genreDao.getGenreByFilmId(1);
		assertThat(genres)
				.isNotNull()
				.isEqualTo(Collections.EMPTY_LIST);
	}

	@Test
	public void deleteAllFilmGenre() {
		Film film = filmBuilder.build();
		filmService.create(film);
		filmDbStorage.addFilmGenre(1, 1);
		filmDbStorage.addFilmGenre(1, 2);

		filmDbStorage.deleteAllFilmGenre(1);

		List<Genre> genreId = genreDao.getGenreByFilmId(1);
		assertThat(genreId)
				.isNotNull()
				.isEqualTo(Collections.EMPTY_LIST);
	}

	@Test
	public void addFriendToUser() {
		User user = userBuilder.build();
		userService.create(user);
		User friend = userBuilder.name("friend").build();
		userService.create(friend);

		friendsDao.addFriend(1, 2);
		List<Integer> friends = friendsDao.getFriendsByUserId(1);
		assertNotNull(friends);
		assertEquals(friends.size(), 1);
		assertEquals(friends.get(0), 2);
	}

	@Test
	public void getFriendUser() {
		User user = userBuilder.build();
		userService.create(user);
		User friend = userBuilder.name("friend").build();
		userService.create(friend);

		List<Integer> friends = friendsDao.getFriendsByUserId(1);
		assertThat(friends)
				.isNotNull()
				.isEqualTo(Collections.EMPTY_LIST);

		friendsDao.addFriend(1, 2);
		friends = friendsDao.getFriendsByUserId(1);
		assertNotNull(friends);
		assertEquals(friends.size(), 1);
		assertEquals(friends.get(0), 2);
	}

	@Test
	public void deleteFriendUser() {
		User user = userBuilder.build();
		userService.create(user);
		User friend = userBuilder.name("friend").build();
		userService.create(friend);
		friendsDao.addFriend(1, 2);
		friendsDao.deleteFriend(1, 2);

		List<Integer> friends = friendsDao.getFriendsByUserId(1);
		assertThat(friends)
				.isNotNull()
				.isEqualTo(Collections.EMPTY_LIST);
	}

	@Test
	public void addLikeToFilm() {
		Film film = filmBuilder.build();
		filmService.create(film);
		User user = userBuilder.build();
		userService.create(user);

		filmService.addLike(1, 1);
		List<Integer> likes = filmService.getLikeByFilmId(1);
		assertNotNull(likes);
		assertEquals(likes.size(), 1);
		assertEquals(likes.get(0), 1);
	}

	@Test
	public void getLikesByFilmId() {
		Film film = filmBuilder.build();
		filmService.create(film);
		List<Integer> likes = filmService.getLikeByFilmId(1);
		assertThat(likes)
				.isNotNull()
				.isEqualTo(Collections.EMPTY_LIST);
	}

	@Test
	public void deleteLikeFilm() {
		Film film = filmBuilder.build();
		filmService.create(film);
		User user = userBuilder.build();
		userService.create(user);
		userService.create(user);
		filmService.addLike(1, 1);
		filmService.addLike(1, 2);

		filmService.deleteLike(1, 2);
		List<Integer> likes = filmService.getLikeByFilmId(1);
		assertNotNull(likes);
		assertEquals(likes.size(), 1);
		assertEquals(likes.get(0), 1);

		filmService.deleteLike(1, 1);
		likes = filmService.getLikeByFilmId(1);
		assertThat(likes)
				.isNotNull()
				.isEqualTo(Collections.EMPTY_LIST);
	}

	@Test
	public void getMpa() {
		List<Mpa> mpas = mpaDao.getRatings();
		assertNotNull(mpas);
		assertEquals(mpas.size(), 5);
		assertThat(mpas.get(0))
				.isNotNull()
				.hasFieldOrPropertyWithValue("id", 1)
				.hasFieldOrPropertyWithValue("name", "G");
	}

	@Test
	public void findMpaById() {
		Mpa mpa = mpaDao.getRatingById(1);
		assertThat(mpa)
				.isNotNull()
				.hasFieldOrPropertyWithValue("id", 1)
				.hasFieldOrPropertyWithValue("name", "G");

		HasNoBeenFoundException ex = assertThrows(
				HasNoBeenFoundException.class,
				() -> mpaDao.getRatingById(-1)
		);
		assertEquals("The object has no been Found", ex.getMessage());

		ex = assertThrows(
				HasNoBeenFoundException.class,
				() -> mpaDao.getRatingById(999)
		);
		assertEquals("The object has no been Found", ex.getMessage());
	}

	@Test
	public void getGenreById() {
		Genre genre = genreDao.getGenreById(1);
		assertThat(genre)
				.isNotNull()
				.hasFieldOrPropertyWithValue("id", 1)
				.hasFieldOrPropertyWithValue("name", "Комедия");

		HasNoBeenFoundException ex = assertThrows(
				HasNoBeenFoundException.class,
				() -> genreDao.getGenreById(-1)
		);
		assertEquals("The object has no been Found", ex.getMessage());

		ex = assertThrows(
				HasNoBeenFoundException.class,
				() -> genreDao.getGenreById(999)
		);
		assertEquals("The object has no been Found", ex.getMessage());
	}

	@Test
	public void getAllGenres() {
		List<Genre> genres = genreDao.getGenres();
		assertNotNull(genres);
		assertEquals(genres.size(), 6);
		assertThat(genres.get(0))
				.hasFieldOrPropertyWithValue("id", 1)
				.hasFieldOrPropertyWithValue("name", "Комедия");
	}

	@Test
	public void genresByFilmId() {
		Film film = filmBuilder.build();
		filmService.create(film);
		List<Genre> genres = genreDao.getGenreByFilmId(1);
		assertThat(genres)
				.isNotNull()
				.isEqualTo(Collections.EMPTY_LIST);
	}
}
