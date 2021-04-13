
### 환경
- Java 8
    - $ sudo apt-get install openjdk-8-jdk
- Gradle
    - $ sudo apt-get install gradle


### 실행 방법
- (프로젝트 루트 디렉토리에서) 터미널에서 아래 명령어를 입력.
- $ ./gradlew build && java -jar build/libs/urlshorten-0.0.1-SNAPSHOT.jar


##### 이슈
- 실제로 접속 가능한 url만 short url을 생성할지?
- url string을 기반으로 해시함수를 통해 short url code 생성할지?
- 중복된 short url code를 생성할 가능성? 
- 요청수 카운트의 엄밀함을 위해 url domain만 저장할지? 프로토콜 처리는?