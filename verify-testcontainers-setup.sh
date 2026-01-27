#!/bin/bash

echo "========================================"
echo "Testcontainers Setup Verification"
echo "========================================"
echo ""

# Check Java
echo "1. Checking Java version..."
java -version 2>&1 | head -1
echo ""

# Check Docker installation
echo "2. Checking Docker installation..."
if command -v docker &> /dev/null; then
    docker --version
    echo "   ✓ Docker is installed"
else
    echo "   ✗ Docker is NOT installed"
    echo "   → Install from https://www.docker.com/products/docker-desktop"
fi
echo ""

# Check if Docker is running
echo "3. Checking if Docker daemon is running..."
if docker ps &> /dev/null; then
    echo "   ✓ Docker daemon is running"
    echo "   → Ready to run tests!"
else
    echo "   ✗ Docker daemon is NOT running"
    echo "   → Please start Docker Desktop and try again"
fi
echo ""

# Check Gradle
echo "4. Checking Gradle..."
if [ -f "./gradlew" ]; then
    echo "   ✓ Gradle wrapper found"
else
    echo "   ✗ Gradle wrapper not found"
fi
echo ""

# Check test files
echo "5. Checking test files..."
if [ -f "src/test/java/org/example/aggrevateddata/TestcontainersConfiguration.java" ]; then
    echo "   ✓ TestcontainersConfiguration.java exists"
else
    echo "   ✗ TestcontainersConfiguration.java missing"
fi

if [ -f "src/test/java/org/example/aggrevateddata/AbstractIntegrationTest.java" ]; then
    echo "   ✓ AbstractIntegrationTest.java exists"
else
    echo "   ✗ AbstractIntegrationTest.java missing"
fi
echo ""

echo "========================================"
echo "Summary"
echo "========================================"
if docker ps &> /dev/null; then
    echo "✓ Ready to run tests with:"
    echo "  ./gradlew test"
else
    echo "⚠ Action required:"
    echo "  1. Start Docker Desktop"
    echo "  2. Wait for it to be ready"
    echo "  3. Run: ./gradlew test"
fi
echo ""
