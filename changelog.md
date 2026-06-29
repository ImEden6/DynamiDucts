# v1.0.1

## Bug Fixes

- Fixed side tab and sheet button hit detection — replaced the pre-filtered `isClickInside` parameter with `isMouseOver()` so clicks respect actual widget bounds.
- Fixed info side tab text rendering — migrated from the deprecated `graphics.text(...)` to the new `graphics.textRenderer()` API, and applied `textColor()` to wrapped lines so they render with the correct color.
