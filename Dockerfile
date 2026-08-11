FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copia todos os arquivos do seu projeto para o container
COPY . .

# Compila o código Java
RUN javac -d bin $(find src -name "*.java")

# Comando para rodar a aplicação
CMD ["java", "-cp", "bin", "microciv.App"]