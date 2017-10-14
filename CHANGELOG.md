## Change Log

### Version 1.9.0 (15 Oct 2017)
- New: Add `@ResponseKeyPath` for successful response parsing
- New: Add `errorMessageKeyPath` for error response parsing
- Api changes: `@Image` is refactored to `@MultiPart` which add mime type parameter
- Fix: Mock reponse parsing now will not throw error when use `@Unwrap`

### Version 1.8.0 (9 Oct 2017)
- Api changes: Demote `ApiClientConfig` to `SimpleApiClient.Config` 

### Version 1.7.0 (9 Oct 2017)
- Api changes: Error model type should be set via `SimpleApierrorClass` 

### Version 1.6.2 (8 Oct 2017)
- Fix: No default parameter value for retry delaySeconds

### Version 1.6.1 (8 Oct 2017)
- Fix: Wrong wording

### Version 1.6.0 (8 Oct 2017)
- New: Support custom JSON parser

### Version 1.5.0 (7 Oct 2017)
- New: Support mock response status

### Version 1.4.1 (7 Oct 2017)
- Fix: Return correct error for status code 500..599

### Version 1.4.0 (6 Oct 2017)
- New: Data mocking support

### Version 1.3.0 (5 Oct 2017)
- Fix: `observe()` now return `Cancellable`

### Version 1.2.0 (5 Oct 2017)
- Fix: Rename `autoDispose()` to `autoCancel()`

### Version 1.1.0 (4 Oct 2017)
- New: Add `enableStetho`, `logLevel` and `certificatePins` configs
