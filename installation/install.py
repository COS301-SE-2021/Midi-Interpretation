# Install the properties folder

properties_contents = \
    "spring.jpa.hibernate.ddl-auto=update\n" \
    "spring.datasource.url=jdbc:mysql://host/database\n" \
    "spring.datasource.username=user\n" \
    "spring.datasource.password=pass\n" \
    "logging.level.root=WARN\n"

properties_file = "application.properties"
with open(properties_file, "w") as f:
    f.write(properties_contents)