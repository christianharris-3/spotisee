.PHONY: local frontend-dev backend-dev down logs

local:
	docker compose \
		-f compose.yml \
		-f compose.local.yml \
		up -d --build

frontend-dev:
	docker compose \
		-f compose.yml \
		-f compose.frontend-dev.yml \
		pull backend

	docker compose \
		-f compose.yml \
		-f compose.frontend-dev.yml \
		up -d --build

backend-dev:
	docker compose \
		-f compose.yml \
		-f compose.backend-dev.yml \
		pull frontend

	docker compose \
		-f compose.yml \
		-f compose.backend-dev.yml \
		up -d --build
rebuild-frontend:
	docker compose \
		-f compose.yml \
		-f compose.frontend-dev.yml \
		up -d --build frontend

rebuild-backend:
	docker compose \
		-f compose.yml \
		-f compose.backend-dev.yml \
		up -d --build backend
down:
	docker compose down

logs:
	docker compose logs -f