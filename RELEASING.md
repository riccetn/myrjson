# Releasing

Releases are driven by git tags. Pushing a tag of the form `vX.Y.Z` runs the
[Release workflow](.github/workflows/release.yml), which:

1. Sets the project version to `X.Y.Z` (taken from the tag).
2. Builds, tests and deploys the jar, sources jar and javadoc jar to
   [GitHub Packages](https://github.com/riccetn/myrjson/packages).
3. Creates a GitHub release for the tag with generated release notes and the
   jars attached.

The version in `pom.xml` on `main` stays a `-SNAPSHOT` version; it is never
committed with a release version.

## Cutting a release

```sh
git checkout main
git pull
git tag -a v0.1.0 -m "Release 0.1.0"
git push origin v0.1.0
```

Release notes are grouped by pull request label as configured in
[`.github/release.yml`](.github/release.yml).

## Using the package

GitHub Packages requires authentication even for public packages. Add a token
with the `read:packages` scope to `~/.m2/settings.xml`:

```xml
<settings>
  <servers>
    <server>
      <id>github-myrjson</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <password>YOUR_TOKEN</password>
    </server>
  </servers>
</settings>
```

Then add the repository and dependency to your `pom.xml`:

```xml
<repositories>
  <repository>
    <id>github-myrjson</id>
    <url>https://maven.pkg.github.com/riccetn/myrjson</url>
  </repository>
</repositories>

<dependency>
  <groupId>se.narstrom.myr</groupId>
  <artifactId>myrjson</artifactId>
  <version>0.1.0</version>
</dependency>
```
