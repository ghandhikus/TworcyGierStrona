@echo off

echo # Backend
cloc src/test src/main/java --quiet

echo source
cloc src/main/java --quiet
echo tests
cloc src/test --quiet
echo.
echo # Frontend
cloc src/main/webapp/WEB-INF --quiet

echo # ALL
cloc src/test src/main/java src/main/webapp/WEB-INF --quiet


@pause