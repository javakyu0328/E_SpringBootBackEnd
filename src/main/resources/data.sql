-- 샘플 영화 데이터 삽입
INSERT INTO movies (title, genre, release_date, description, poster_url, recommendation_count)
VALUES
    ('인셉션', '액션, SF', '2010-07-21', '꿈과 현실의 경계를 넘나드는 액션 블록버스터', 'https://example.com/inception.jpg', 10),
    ('어벤져스: 엔드게임', '액션, 모험', '2019-04-24', '인피니티 워 이후 절반만 남은 지구를 지키기 위한 영웅들의 마지막 전투', 'https://example.com/avengers.jpg', 8),
    ('기생충', '드라마, 스릴러', '2019-05-30', '전원백수로 살 길 막막하지만 사이는 좋은 기택 가족', 'https://example.com/parasite.jpg', 15),
    ('미나리', '드라마', '2021-03-03', '1980년대 미국 아칸소로 이민 온 한국 가족의 이야기', 'https://example.com/minari.jpg', 7),
    ('라라랜드', '뮤지컬, 로맨스', '2016-12-07', '꿈을 꾸는 사람들을 위한 아름다운 뮤지컬 영화', 'https://example.com/lalaland.jpg', 12),
    ('조커', '범죄, 드라마', '2019-10-02', '고담시의 광대 아서 플렉이 조커로 변모하는 과정', 'https://example.com/joker.jpg', 9),
    ('1917', '전쟁, 드라마', '2020-02-19', '제1차 세계대전 중 두 병사의 불가능한 임무', 'https://example.com/1917.jpg', 6),
    ('소울', '애니메이션, 가족', '2020-12-25', '음악에 모든 것을 걸었던 조가 영혼의 세계로 들어가게 되는 이야기', 'https://example.com/soul.jpg', 11),
    ('테넷', '액션, SF', '2020-08-26', '시간의 흐름을 뒤집는 인버전을 통해 현재와 미래를 오가며 세상을 구하는 이야기', 'https://example.com/tenet.jpg', 5),
    ('미드소마', '공포, 미스터리', '2019-07-03', '스웨덴의 한 마을에서 열리는 90년만의 하지 축제에 참여한 대학생들의 이야기', 'https://example.com/midsommar.jpg', 4);

-- 샘플 추천 데이터 삽입
INSERT INTO movie_recommendations (movie_id, member_id)
VALUES
    (1, 'user1'),
    (1, 'user2'),
    (1, 'user3'),
    (2, 'user1'),
    (2, 'user2'),
    (3, 'user1'),
    (3, 'user2'),
    (3, 'user3'),
    (3, 'user4'),
    (4, 'user2'),
    (5, 'user1'),
    (5, 'user3'),
    (6, 'user4'),
    (7, 'user1'),
    (8, 'user2'),
    (8, 'user3'),
    (9, 'user4'),
    (10, 'user1');