xcopy "frontend/root" "backend/web-service/src/main/resources/frontend" /E/H
docker build --no-cache -t turtleshelldevelopment:database ./database
docker build --no-cache -t turtleshelldevelopment:backend ./backend/web-service
docker stack deploy --compose-file docker-compose.yml IS241