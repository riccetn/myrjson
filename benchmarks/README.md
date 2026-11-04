# MyrJSON benchmarks

[JMH](https://github.com/openjdk/jmh) benchmarks comparing MyrJSON with
[Eclipse Parsson](https://github.com/eclipse-ee4j/parsson), the reference
implementation of Jakarta JSON Processing.

This is a standalone Maven project. It is not part of the main build, so the
benchmarks never run in CI or slow down the release.

## Running

The benchmarks use the MyrJSON jar from the local Maven repository, so install
it first:

```sh
mvn install -DskipTests          # from the repository root
cd benchmarks
mvn package
java -jar target/benchmarks.jar
```

The full suite takes a long time. JMH options narrow it down:

```sh
# Only the parsing benchmarks, on the large document
java -jar target/benchmarks.jar ParseBenchmark -p data=LARGE

# A quick smoke run, useful for checking that everything works (not for real numbers)
java -jar target/benchmarks.jar -f 1 -wi 1 -i 1 -w 1 -r 1

# Save results as JSON, e.g. for https://jmh.morethan.io
java -jar target/benchmarks.jar -rf json -rff results.json

# List all benchmarks and options
java -jar target/benchmarks.jar -l
java -jar target/benchmarks.jar -h
```

## What is measured

Each benchmark runs for every combination of these parameters:

* `implementation`: `MYRJSON` or `PARSSON`. Each provider is instantiated
  directly, not looked up through `ServiceLoader`.
* `data`: `SMALL` (about 2 KB), `MEDIUM` (about 110 KB) or `LARGE` (about
  1.8 MB). Each is a deterministically generated document of records that
  mix strings (including escapes and non-ASCII characters), integers,
  decimals, booleans, nulls, nested objects and arrays.

### Parsing (`ParseBenchmark`)

| Benchmark                      | API                                                        |
|--------------------------------|------------------------------------------------------------|
| `streamParseReader`            | `JsonParser` from a `Reader`; every event and value is read |
| `streamParseInputStream`       | `JsonParser` from an `InputStream` with explicit UTF-8      |
| `streamParseInputStreamDetect` | `JsonParser` from an `InputStream` with charset detection   |
| `readTreeReader`               | `JsonReader.readValue()` from a `Reader`                    |
| `readTreeInputStream`          | `JsonReader.readValue()` from an `InputStream` (UTF-8)      |

### Generating (`GenerateBenchmark`)

The input is the same document, read during setup into the object model of the
implementation under test.

| Benchmark                    | API                                                           |
|------------------------------|---------------------------------------------------------------|
| `streamGenerateWriter`       | `JsonGenerator` to a `Writer`, one call per value               |
| `streamGenerateOutputStream` | `JsonGenerator` to an `OutputStream` (UTF-8)                    |
| `streamGeneratePretty`       | `JsonGenerator` with `JsonGenerator.PRETTY_PRINTING`            |
| `writeTreeWriter`            | `JsonWriter.write()` of an object model to a `Writer`           |
| `writeTreeOutputStream`      | `JsonWriter.write()` of an object model to an `OutputStream`    |
| `buildTree`                  | Building the object model with `JsonObjectBuilder`/`JsonArrayBuilder` |
| `buildAndWriteTree`          | Building the object model and then writing it with `JsonWriter` |

JSON Pointer, JSON Patch, JSON Merge Patch and diff are not benchmarked.
