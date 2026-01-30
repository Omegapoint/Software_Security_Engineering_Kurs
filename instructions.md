# Software Security Engineering Course
## Building a Secure Supply Chain with GitHub Actions

Welcome to this hands-on course! In this course, you will progressively add security tooling to your GitHub Actions workflow. Your goal is to identify and understand security vulnerabilities in the Todo application by implementing industry-standard security scanning tools.

---

## Step 1: Static Application Security Testing (SAST) with Semgrep

### 📚 Tool Overview: Semgrep

**What is Semgrep?**

Semgrep is a static analysis tool that scans your source code for security vulnerabilities, code quality issues, and compliance violations **without executing the code**. It uses pattern-based rules to detect problems in your codebase.

**Aim & Purpose:**

- Identify security vulnerabilities early in the development process
- Detect coding patterns that could lead to security issues
- Provide fast, accurate feedback on code quality
- Support multiple programming languages including Java

**Key Benefits:**
- Runs in the CI/CD pipeline automatically
- Identifies issues before they reach production
- Helps developers learn secure coding practices
- Community-driven rule set with many pre-built rules

### 🔗 Documentation & Resources

- **Semgrep Official Documentation:** https://semgrep.dev/docs/
- **Semgrep Rules Library:** https://semgrep.dev/r
- **Community vs Pro:** https://semgrep.dev/pricing

### ✅ Your Task: Step 1

1. **Add Semgrep to your `security.yml`** workflow file
   - Use the Semgrep GitHub Action for community version
   - Configure it to scan the `todo-app` directory
   - Set it to use the community rule set (free, open-source rules)

2. **Run the GitHub Actions workflow**
   - Commit your `security.yml` changes to a new branch
   - Create a pull request to trigger the workflow
   - Navigate to the **Actions** tab in your GitHub repository
   - Wait for the Semgrep job to complete

3. **Review the Results**
   - Go to the **Security** tab → **Code scanning alerts**
   - Examine the vulnerabilities found by Semgrep
   - Note the severity levels and descriptions of each finding

4. **Reflection Question:**

   > **Have you identified something strange or unexpected in the Semgrep results?**
   > 
   > - Are there false positives?
   > - Did it find security issues you expected?
   > - Are there any unusual patterns flagged?
   > 
   > Document your observations before moving to the next step.

### ⏱️ If You Have Time Left...

**Challenge:** Attempt to fix one or two of the vulnerabilities identified by Semgrep in the `todo-app` source code.

- Focus on high-severity issues first
- Make changes to the Java code to resolve the security findings
- Re-run the Semgrep workflow to verify the fixes
- Observe how the results change

---

## Step 2: Dependency Vulnerability Scanning with Dependabot

### 📚 Tool Overview: Dependabot

**What is Dependabot?**

Dependabot is GitHub's built-in dependency management tool that automatically scans your project's dependencies for known security vulnerabilities. It monitors libraries used in your application and alerts you when vulnerabilities are discovered.

**Aim & Purpose:**

- Monitor third-party dependencies for known vulnerabilities
- Automatically detect when security patches are available
- Provide alerts before vulnerabilities are exploited
- Help maintain up-to-date and secure dependencies

**Key Benefits:**
- Integrated directly into GitHub (no additional tools needed)
- Automatic detection of vulnerable versions
- Can automatically create pull requests with updates
- Supports multiple languages and package managers (Maven, npm, pip, etc.)

### 🔗 Documentation & Resources

- **Dependabot Documentation:** https://docs.github.com/en/code-security/dependabot
- **Dependabot Configuration:** https://docs.github.com/en/code-security/dependabot/dependabot-version-updates/about-dependabot-version-updates
- **GitHub Security Advisories:** https://github.blog/security/

### ✅ Your Task: Step 2

1. **Activate Dependabot through GitHub**
   - Navigate to your repository
   - Go to **Settings** → **Code security and analysis**
   - Enable **Dependabot alerts**
   - Enable **Dependabot security updates** (optional, allows auto-PR creation)

2. **Check Dependabot Results**
   - Navigate to the **Security** tab → **Dependabot alerts**
   - Review all dependency vulnerabilities identified
   - Check the severity and affected versions of libraries
   - Note which dependencies in your `pom.xml` have security issues

3. **Analyze the Findings**
   - Compare the Semgrep results (code-level issues) with Dependabot results (dependency issues)
   - These are complementary tools finding different types of problems

4. **Reflection Question:**

   > **What dependencies does Dependabot report as vulnerable?**
   > 
   > - Are these critical, high, or low severity?
   > - Do any of these dependencies directly impact your Todo app's functionality?
   > - Are there newer versions available?
   > 
   > Document your findings before moving to the next step.

### ⏱️ If You Have Time Left...

**Challenge:** Update one or more vulnerable dependencies to their patched versions in the `pom.xml`.

- Identify a vulnerable dependency with an available patch
- Update the version in your `pom.xml`
- Run `mvn clean verify` locally to ensure no breaking changes
- Commit the update and push to trigger Dependabot to re-scan
- Verify the alert is resolved

---

## Step 3: Container Image Scanning with Trivy

### 📚 Tool Overview: Trivy

**What is Trivy?**

Trivy is a comprehensive vulnerability scanner developed by Aqua Security that scans container images, filesystems, and source code for security issues. It's lightweight, fast, and requires no prior setup or external databases.

**Aim & Purpose:**

- Scan Docker images for vulnerabilities before deployment
- Detect misconfigurations in Dockerfile and container environments
- Identify vulnerable packages within the base image
- Ensure container security across your supply chain

**Key Benefits:**
- Lightweight and fast scanning
- Supports multiple scan types (images, filesystems, repositories)
- High accuracy with low false positives
- Integrated with CI/CD pipelines easily
- Scans dependencies in application code as well

### 🔗 Documentation & Resources

- **Trivy Official Documentation:** https://aquasecurity.github.io/trivy/
- **Trivy GitHub Repository:** https://github.com/aquasecurity/trivy
- **Trivy Security Best Practices:** https://aquasecurity.github.io/trivy/latest/

### ✅ Your Task: Step 3

1. **Add Trivy to your `security.yml` workflow**
   - Integrate Trivy image scanning into your GitHub Actions workflow
   - Configure it to scan the Docker image you build from your `Dockerfile`
   - Set it to fail on critical or high-severity vulnerabilities (configurable)

2. **Run the GitHub Actions Workflow**
   - Commit your updated `security.yml` to your branch
   - Push the changes to trigger the workflow
   - Navigate to the **Actions** tab
   - Wait for the Trivy scanning job to complete

3. **Review Container Security Findings**
   - Check the workflow logs or SARIF report
   - Navigate to **Security** tab → **Code scanning alerts**
   - Look for vulnerabilities tagged as "Trivy" or "container"
   - Examine the base image vulnerabilities

4. **Reflection Question:**

   > **What vulnerabilities were found in your container image?**
   > 
   > - Are vulnerabilities in the base image (Alpine, Ubuntu) or your application?
   > - How does this compare to code vulnerabilities found by Semgrep?
   > - Are there misconfigurations in the Dockerfile?
   > 
   > Document your observations before moving to the next step.

### ⏱️ If You Have Time Left...

**Challenge:** Harden your Dockerfile to reduce the vulnerability surface.

- Consider using a minimal base image (Alpine, distroless)
- Remove unnecessary packages from the image
- Run the container as a non-root user
- Re-run Trivy and observe improvements in scan results
- Document what changes reduced the vulnerability count

---

## Step 4: Secret Detection with Trufflehog

### 📚 Tool Overview: Trufflehog

**What is Trufflehog?**

Trufflehog is a secret scanning tool developed by Truffle Security that detects secrets, credentials, and sensitive information that may have been accidentally committed to your repository. It uses pattern matching and entropy analysis to find exposed credentials.

**Aim & Purpose:**

- Prevent accidental exposure of secrets (API keys, passwords, tokens)
- Detect hardcoded credentials before they reach production
- Scan commit history to find previously exposed secrets
- Protect against credential-based attacks

**Key Benefits:**
- Detects multiple types of secrets (AWS keys, private keys, tokens, etc.)
- Uses entropy analysis for high accuracy
- Can scan entire Git history, not just recent commits
- Integrates easily into CI/CD workflows
- Helps enforce secure credential management practices

### 🔗 Documentation & Resources

- **Trufflehog Official Documentation:** https://trufflesecurity.com/trufflehog
- **Trufflehog GitHub Repository:** https://github.com/trufflesecurity/trufflehog
- **Credential Management Best Practices:** https://docs.github.com/en/code-security/secret-scanning

### ✅ Your Task: Step 4

1. **Add Trufflehog to your `security.yml` workflow**
   - Integrate Trufflehog into your GitHub Actions workflow
   - Configure it to scan your entire repository
   - Set it to check the commit history as well

2. **Run the GitHub Actions Workflow**
   - Commit your updated `security.yml` with Trufflehog
   - Push the changes to trigger the workflow
   - Navigate to the **Actions** tab
   - Wait for the Trufflehog scanning job to complete

3. **Review Secret Detection Results**
   - Check the workflow logs for any detected secrets
   - Navigate to **Security** tab if results are reported
   - Look for any false positives (detected secrets that aren't real)
   - Review the commit history findings

4. **Reflection Question:**

   > **Did Trufflehog find any secrets or credentials?**
   > 
   > - Are they false positives (not actual secrets)?
   > - If real credentials were found, how would you remediate?
   > - Are there any credentials in your sample code or test files?
   > 
   > Document your findings and consider using `.env` files and environment variables for credentials.

### ⏱️ If You Have Time Left...

**Challenge:** Implement secure credential handling practices in your application.

- If secrets were detected, remove them from the codebase
- Implement environment variable usage for sensitive configuration
- Add `.env` to your `.gitignore` to prevent accidental commits
- Use GitHub Secrets for CI/CD credentials instead
- Re-run Trufflehog to confirm secrets are removed

---

## Course Completion Summary

### 📊 What You've Accomplished

By completing all four steps, you have implemented a comprehensive security tooling pipeline that covers:

1. **SAST (Code Level)** - Semgrep: Code vulnerabilities and patterns
2. **Dependency Scanning** - Dependabot: Third-party library vulnerabilities
3. **Container Security** - Trivy: Image and infrastructure vulnerabilities
4. **Secret Detection** - Trufflehog: Exposed credentials and secrets

### 🎯 Key Takeaways

- **Layered Security:** Multiple tools catch different types of vulnerabilities
- **Continuous Monitoring:** Automated scanning catches issues early
- **Developer Responsibility:** Understanding and fixing issues is crucial
- **Shared Responsibility:** Security is a team effort across development, ops, and security

### 📚 Next Steps (Optional)

If you want to deepen your security knowledge:

- Explore **SBOM (Software Bill of Materials)** generation with tools like CycloneDX
- Implement **infrastructure-as-code scanning** with tools like Checkov
- Add **runtime security monitoring** for production environments
- Study the **OWASP Top 10** vulnerabilities in more detail
- Research **DevSecOps** practices and maturity models

### 🔗 Useful Resources

- **OWASP Top 10:** https://owasp.org/www-project-top-ten/
- **CWE Top 25:** https://cwe.mitre.org/top25/
- **GitHub Security Documentation:** https://docs.github.com/en/code-security
- **DevSecOps Principles:** https://www.devsecops.org/

---

## Questions & Support

If you encounter any issues or have questions:

1. Check the tool's official documentation (links provided above)
2. Review the GitHub Actions workflow logs for detailed error messages
3. Consult your course instructor or teaching assistant
4. Check the repositories' issue trackers for similar problems

Good luck, and happy learning! 🔐
