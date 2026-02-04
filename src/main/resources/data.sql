-- 견종 (Breed) 데이터
INSERT INTO breed (id, name, size, created_at, updated_at) VALUES (1, '말티즈', 'SMALL', NOW(), NOW());
INSERT INTO breed (id, name, size, created_at, updated_at) VALUES (2, '푸들', 'SMALL', NOW(), NOW());
INSERT INTO breed (id, name, size, created_at, updated_at) VALUES (3, '비숑 프리제', 'SMALL', NOW(), NOW());
INSERT INTO breed (id, name, size, created_at, updated_at) VALUES (15, '포메라니안', 'SMALL', NOW(), NOW());
INSERT INTO breed (id, name, size, created_at, updated_at) VALUES (20, '골든 리트리버', 'LARGE', NOW(), NOW());
INSERT INTO breed (id, name, size, created_at, updated_at) VALUES (21, '시베리안 허스키', 'LARGE', NOW(), NOW());

-- 성격 (Personality) 데이터
INSERT INTO personality (id, name, code, created_at, updated_at) VALUES (1, '소심해요', 'SHY', NOW(), NOW());
INSERT INTO personality (id, name, code, created_at, updated_at) VALUES (2, '에너지넘침', 'ENERGETIC', NOW(), NOW());
INSERT INTO personality (id, name, code, created_at, updated_at) VALUES (3, '간식좋아함', 'TREAT_LOVER', NOW(), NOW());
INSERT INTO personality (id, name, code, created_at, updated_at) VALUES (4, '사람좋아함', 'PEOPLE_LOVER', NOW(), NOW());
INSERT INTO personality (id, name, code, created_at, updated_at) VALUES (5, '친구구함', 'SEEKING_FRIENDS', NOW(), NOW());
INSERT INTO personality (id, name, code, created_at, updated_at) VALUES (6, '주인바라기', 'OWNER_FOCUSED', NOW(), NOW());
INSERT INTO personality (id, name, code, created_at, updated_at) VALUES (7, '까칠해요', 'GRUMPY', NOW(), NOW());

-- 산책 스타일 (WalkingStyle) 데이터
INSERT INTO walking_style (id, name, code, created_at, updated_at) VALUES (1, '전력질주', 'ENERGY_BURST', NOW(), NOW());
INSERT INTO walking_style (id, name, code, created_at, updated_at) VALUES (2, '냄새맡기집중', 'SNIFF_EXPLORER', NOW(), NOW());
INSERT INTO walking_style (id, name, code, created_at, updated_at) VALUES (3, '공원벤치휴식형', 'BENCH_REST', NOW(), NOW());
INSERT INTO walking_style (id, name, code, created_at, updated_at) VALUES (4, '느긋함', 'RELAXED', NOW(), NOW());
INSERT INTO walking_style (id, name, code, created_at, updated_at) VALUES (5, '냄새탐정', 'SNIFF_DETECTIVE', NOW(), NOW());
INSERT INTO walking_style (id, name, code, created_at, updated_at) VALUES (6, '무한동력', 'ENDLESS_ENERGY', NOW(), NOW());
INSERT INTO walking_style (id, name, code, created_at, updated_at) VALUES (7, '저질체력', 'LOW_STAMINA', NOW(), NOW());