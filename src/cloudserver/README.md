# Cloud Server Builder

## What is this?

A Java implementation of the **Builder** creational design pattern, modeled
around configuring a cloud server (similar to launching a virtual machine
on AWS/Azure/GCP). The server is kept deliberately simple with only
**3 attributes**: region, CPU cores, and RAM — enough to demonstrate the
pattern clearly without unnecessary complexity.

## Structure

| Class                   | Role                                                             |
|--------------------------|--------------------------------------------------------------------|
| `CloudServer`             | **Product** — the finished, immutable server configuration.       |
| `CloudServer.Builder`    | **Builder** — nested static class with a fluent API.              |
| `CloudServerDirector`    | **Director** — known configs (Dev server, Production server).    |
| `Main`                    | **Client** — demonstrates custom builds, Director builds, and validation. |

The Builder is implemented as a `public static class` **nested inside**
`CloudServer`, rather than as a separate top-level class. This lets
`CloudServer`'s private constructor read the Builder's fields directly
(`builder.region`, `builder.cpuCores`, ...), since a nested class and its
enclosing class share access to each other's private members in Java.

## How to run

```bash
javac -d out src/cloudserver/*.java
java -cp out cloudserver.Main
```

Expected output:
```
eu-west-1 | 4 vCPU | 16GB RAM
eu-west-1 | 2 vCPU | 4GB RAM
us-east-1 | 8 vCPU | 32GB RAM
Rejected as expected: region is null or blank
```

## Clean Code principles applied

### 1. Meaningful, intention-revealing names

Class and method names describe exactly what they do:
`CloudServerDirector.makeProductionServer()`, `CloudServer.getRegion()`,
`toSummaryLine()`. A reader does not need to open the method body to
guess what it returns.

*Note on consistency:* one setter in this project, `setcpuCores()`, uses
a lowercase `c` where Java convention (and the other two setters,
`setRegion()` / `setRamGB()`) would suggest `setCpuCores()`. This is a
useful before/after example of the principle itself:

```java
// BEFORE — trying to support different combinations of parameters without a Builder
public CloudServer(String region) { ... }
public CloudServer(String region, int cpuCores) { ... }
public CloudServer(String region, int cpuCores, int ramGB) { ... }
// What if you need region + ramGB, but without cpuCores? Yet another constructor...

// AFTER — the Builder itself handles any combination of fields
new CloudServer.Builder().setRegion("eu-west-1").build();                // one field
new CloudServer.Builder().setRegion("eu-west-1").setCpuCores(4).build(); // two fields
```

### 2. Small methods, each doing one thing

Every setter in `Builder` does exactly one job — set one field and
return `this` — with no side effects or hidden validation mixed in.
All "is the object fully ready" logic lives in a single place, `build()`,
not spread across setters.

```java
public Builder setRegion(String region) {
    this.region = region;
    return this;
}
```

### 3. Validated construction — `build()` fails fast on invalid state

A server without a region, without CPU cores, or without RAM is not a
valid, deployable configuration. Instead of silently producing a broken
`CloudServer`, `build()` checks every required field and throws a clear
exception explaining exactly what is missing:

```java
public CloudServer build() {
    if (region == null || region.isBlank()) {
        throw new IllegalStateException("region is null or blank");
    }
    if (cpuCores <= 0) {
        throw new IllegalStateException("cpuCores must be greater than 0");
    }
    if (ramGB <= 0) {
        throw new IllegalStateException("ramGB must be greater than 0");
    }
    return new CloudServer(this);
}
```

This is demonstrated directly in `Main`, where an intentionally
incomplete builder (missing `setRegion`) is caught and reported instead
of crashing the whole program:

```java
try {
    CloudServer invalidServer = new CloudServer.Builder()
            .setcpuCores(4)
            .setRamGB(8)
            .build();
} catch (IllegalStateException e) {
    System.out.println("Rejected as expected: " + e.getMessage());
}
```

### 4. Controlled object creation (private constructor)

`CloudServer`'s constructor is `private`, so the only way to obtain a
`CloudServer` instance anywhere in the codebase is through
`CloudServer.Builder().build()`. This makes it impossible to
accidentally create an incomplete or invalid `CloudServer` by bypassing
the Builder entirely.

```java
private CloudServer(Builder builder) {
    this.region = builder.region;
    this.cpuCores = builder.cpuCores;
    this.ramGB = builder.ramGB;
}
```

### 5. Small, focused classes (Single Responsibility)

`CloudServer.Builder` only knows how to *assemble* a server one field at
a time. `CloudServerDirector` only knows *which combinations* of
settings are common, reusable configurations (dev vs. production). Each
class has exactly one reason to change.

```java
public CloudServer makeDevServer(CloudServer.Builder builder) {
    return builder
            .setRegion("eu-west-1")
            .setcpuCores(2)
            .setRamGB(4)
            .build();
}

public CloudServer makeProductionServer(CloudServer.Builder builder) {
    return builder
            .setRegion("us-east-1")
            .setcpuCores(8)
            .setRamGB(32)
            .build();
}
```

## Multiple representations (Product)

`CloudServer` exposes its data through:
- `toSummaryLine()` — a single-line, human-readable summary.
- `getRegion()` / `getCpuCores()` / `getRamGB()` — individual field access
  for any code that needs a specific value rather than the whole string.
