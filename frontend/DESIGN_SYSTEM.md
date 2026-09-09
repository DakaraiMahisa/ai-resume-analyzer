# Design System — Rulebook

Read this document before writing or editing any UI code in this repository.

These rules apply equally to human contributors and AI coding tools. They are architectural rules, not suggestions.

---

## 1. Design System Architecture

The application follows a layered design-system architecture:

```text
src/
├── styles/
│   ├── tokens.css
│   └── theme.css
│
├── components/
│   ├── ui/
│   │   ├── Button
│   │   ├── Card
│   │   ├── Input
│   │   ├── Select
│   │   ├── Textarea
│   │   ├── Modal
│   │   ├── Table
│   │   ├── Badge
│   │   ├── Progress
│   │   └── AppToaster
│   │
│   └── layout/
│       ├── Sidebar
│       ├── Topbar
│       ├── UserMenu
│       ├── AppLayout
│       ├── AuthLayout
│       └── AuthCard
│
└── features/
    ├── auth/
    ├── resume/
    ├── job-description/
    ├── analysis/
    ├── rie/
    └── ats/
```

The styling system itself follows this flow:

```text
tokens.css
    ↓
Primitive design values
    ↓
theme.css
    ↓
Semantic application tokens
    ↓
Tailwind @theme
    ↓
UI primitives / layouts / features
```

The fundamental rule is:

> **Define values once, give them semantic meaning once, and consume that meaning everywhere.**

---

# 2. The Four UI Layers

## Layer 1 — `tokens.css`

Contains primitive design values.

Examples:

```css
--brand-600
--neutral-900
--space-4
--radius-md
--shadow-md
--font-size-base
```

These values describe the available design materials.

They do **not** describe what those values mean inside the application.

---

## Layer 2 — `theme.css`

Contains semantic application tokens and theme behavior.

Examples:

```css
--theme-background
--theme-surface
--theme-text-primary
--theme-border
--theme-brand
--theme-danger
--theme-match
--theme-processing
```

It also:

- defines light and dark themes
- maps semantic tokens into Tailwind's `@theme`
- provides the shadcn/ui compatibility bridge
- establishes global base-layer defaults

Components consume the Tailwind-facing semantic names:

```text
bg-background
bg-surface
text-text-primary
text-text-secondary
border-border
bg-brand
text-match
```

Components should not consume primitive ramps directly.

---

## Layer 3 — `components/`

Reusable UI building blocks live here.

### `components/ui`

These are generic application primitives:

- Button
- Card
- Input
- Select
- Textarea
- Modal
- Table
- Badge
- Progress
- AppToaster

Each primitive is styled once.

A feature should compose these components rather than recreate their styling.

### `components/layout`

These provide application-level scaffolding:

- Sidebar
- Topbar
- UserMenu
- AppLayout
- AuthLayout
- AuthCard

Layout components may consume semantic tokens directly and compose `components/ui` primitives.

---

## Layer 4 — `features/`

Feature modules contain application-specific UI.

Examples:

```text
features/auth/
features/resume/
features/job-description/
features/analysis/
features/rie/
features/ats/
```

Feature pages should compose existing UI and layout primitives.

They should not introduce their own design language.

---

# 3. The Most Important Rule

> **Never write a raw design value inside `components/` or `features/` when that value should be controlled by the design system.**

Avoid:

```tsx
<div className="bg-white text-gray-900">
```

Avoid:

```tsx
<div className="bg-indigo-600">
```

Avoid:

```tsx
<div className="rounded-[13px]">
```

Avoid:

```tsx
<div style={{ color: "#4f46e5" }}>
```

Prefer:

```tsx
<div className="bg-surface text-text-primary">
```

```tsx
<div className="bg-brand">
```

```tsx
<div className="rounded-md">
```

If the required design value does not exist:

```text
1. Add the primitive value to tokens.css.
2. Give it semantic meaning in theme.css.
3. Expose it through @theme if required.
4. Consume the semantic utility in the component.
```

Never bypass the layers.

---

# 4. `tokens.css` — Primitive Values

`tokens.css` is the foundation of the design system.

It contains raw values such as:

### Color ramps

```css
--brand-50 ... --brand-900
--neutral-0 ... --neutral-950
--success-50 ... --success-900
--warning-50 ... --warning-900
--danger-50 ... --danger-900
--info-50 ... --info-900
```

### Spacing

```css
--space-1
--space-2
--space-3
--space-4
...
```

The system uses a 4px base spacing unit.

### Radius

```css
--radius-sm
--radius-md
--radius-lg
--radius-xl
--radius-2xl
--radius-full
```

### Shadows

```css
--shadow-sm
--shadow-md
--shadow-lg
--shadow-xl
```

### Typography

Primitive typography values include font families, font sizes, weights, and line heights.

### Z-index

```css
--z-base
--z-dropdown
--z-sticky
--z-overlay
--z-modal
--z-popover
--z-toast
--z-tooltip
```

### Motion

Animation durations are also centralized here.

---

## What does NOT belong in `tokens.css`

Do not put semantic application concepts here.

Incorrect:

```css
--resume-match: green;
--upload-button: blue;
--ats-warning: orange;
```

Those are semantic concepts and belong in `theme.css`.

A primitive palette is a palette.

It does not know what the application will use it for.

---

# 5. `theme.css` — Semantic Meaning

`theme.css` gives primitive values meaning.

For example:

```css
--theme-brand: var(--brand-600);
--theme-background: var(--neutral-50);
--theme-text-primary: var(--neutral-900);
```

The component therefore does not need to know that the brand happens to use `brand-600`.

It only knows:

```tsx
className = "bg-brand";
```

This separation allows the visual implementation to change without rewriting components.

---

# 6. Semantic Tokens

Prefer semantic names over implementation names.

Good:

```text
background
surface
text-primary
text-secondary
border
brand
success
warning
danger
processing
match
partial
mismatch
```

Bad:

```text
purple
blue
green
gray
indigo-600
```

The first group describes **meaning**.

The second group describes **appearance**.

Components should depend on meaning.

---

# 7. Analysis Semantics

AI Resume Analyzer has domain-specific visual states.

These are deliberately represented as semantic tokens.

```text
match
partial
mismatch
processing
analysis-neutral
```

### Match

Represents strong requirement alignment or verified evidence.

```tsx
text - match;
bg - match - tint;
border - match;
```

### Partial

Represents incomplete alignment, incomplete evidence, or a result requiring review.

```tsx
text - partial;
bg - partial - tint;
```

### Mismatch

Represents a requirement mismatch, unsupported evidence, or conflict.

```tsx
text - mismatch;
bg - mismatch - tint;
```

### Processing

Represents active AI analysis, asynchronous processing, or pipeline activity.

```tsx
text - processing;
bg - processing - tint;
```

### Analysis neutral

Represents states such as:

- pending
- not evaluated
- unavailable

```tsx
text - analysis - neutral;
bg - analysis - neutral - tint;
```

RIE and ATS modules should use these semantic concepts rather than directly selecting generic success, warning, or danger colors.

---

# 8. Dark Mode

Dark mode is class-based.

The application applies:

```html
<html class="dark"></html>
```

when dark mode is active.

The semantic tokens are changed inside the `.dark` block in `theme.css`.

For example:

```css
:root {
  --theme-background: var(--neutral-50);
}

.dark {
  --theme-background: var(--neutral-950);
}
```

Components therefore remain unchanged.

They continue using:

```tsx
className = "bg-background";
```

The meaning of `background` changes underneath them.

---

## Dark Mode Rule

Do not manually compensate for dark mode inside components.

Avoid:

```tsx
className = "bg-white dark:bg-neutral-900";
```

Prefer:

```tsx
className = "bg-surface";
```

If the dark appearance is wrong, fix the semantic token in `theme.css`.

The component should not know how light and dark themes are implemented.

---

# 9. Tailwind Contract

Tailwind v4 consumes the semantic design system through `@theme` in `theme.css`.

For example:

```css
@theme {
  --color-background: var(--theme-background);
  --color-surface: var(--theme-surface);
  --color-text-primary: var(--theme-text-primary);
  --color-brand: var(--theme-brand);
}
```

This produces utilities such as:

```tsx
bg - background;
bg - surface;
text - text - primary;
text - brand;
```

The intended dependency is:

```text
Component
    ↓
Tailwind utility
    ↓
Semantic theme token
    ↓
Primitive token
```

Never reverse this dependency.

---

# 10. Naming Collision Rule

Primitive tokens and Tailwind-facing tokens must not accidentally reference themselves.

Incorrect:

```css
--font-sans: var(--font-sans);
--text-xs: var(--text-xs);
```

This creates a self-reference rather than a meaningful mapping.

Primitive tokens should have distinct names from their Tailwind-facing counterparts when a mapping is required.

For example:

```css
/* tokens.css */

--font-family-sans: "Inter", sans-serif;
--font-size-base: 15px;
```

Then:

```css
/* theme.css */

@theme {
  --font-sans: var(--font-family-sans);
  --text-base: var(--font-size-base);
}
```

The dependency is now explicit:

```text
primitive
--font-size-base
       ↓
Tailwind token
--text-base
       ↓
utility
text-base
```

---

# 11. shadcn/ui Compatibility

shadcn/ui components use a predefined semantic vocabulary.

Examples include:

```text
primary
secondary
muted
accent
destructive
foreground
card
popover
ring
input
```

The application does not change this vocabulary inside generated components.

Instead, `theme.css` provides the compatibility bridge.

For example:

```css
--color-primary: var(--theme-brand);
--color-destructive: var(--theme-danger);
--color-muted: var(--theme-surface-subtle);
```

This means shadcn-generated components can remain conventional while still following our design system.

If a new shadcn component requires a semantic variable that does not exist:

> Add it to the compatibility bridge in `theme.css`.

Do not hardcode the value inside the generated component.

---

# 12. Global Base Layer

`theme.css` contains application-wide defaults in `@layer base`.

The global border color is controlled by the semantic border token.

The body uses the application's semantic text and background tokens.

Conceptually:

```css
@layer base {
  * {
    border-color: var(--theme-border);
  }

  body {
    color: var(--theme-text-primary);
    background-color: var(--theme-background);
  }
}
```

This ensures that browser defaults do not silently introduce colors outside the design system.

If an unstyled element has the wrong default text, background, or border color, check the global base layer before adding a local workaround.

---

# 13. Component Styling Rules

UI primitives should use semantic utilities.

Good:

```tsx
<Button className="mt-4">
```

The `mt-4` only controls local layout.

Good:

```tsx
<Card className="mt-4">
```

Bad:

```tsx
<Card className="bg-white border-gray-200">
```

A component may receive layout-specific classes from a page.

It should not receive one-off visual overrides that undermine the design system.

---

# 14. Interactive Controls

Inputs, selects, textareas, and similar controls must explicitly support the application's semantic surface and text colors.

Prefer:

```tsx
className = "bg-surface text-text-primary";
```

rather than relying on browser inheritance.

This is particularly important for dark mode.

---

# 15. Z-Index

Do not invent arbitrary stacking values such as:

```tsx
z - 30;
z - 50;
```

Use the application's z-index scale:

```tsx
z - base;
z - dropdown;
z - sticky;
z - overlay;
z - modal;
z - popover;
z - toast;
z - tooltip;
```

The purpose is predictable stacking order.

---

# 16. Shadows, Radius, and Spacing

Do not introduce arbitrary design values when an existing token covers the requirement.

Avoid:

```tsx
rounded-[13px]
shadow-[0_3px_7px_rgba(...)]
p-[17px]
```

Prefer:

```tsx
rounded - md;
shadow - sm;
p - 4;
```

If a value genuinely represents a new design-system requirement, add it to the token system first.

---

# 17. Common Mistakes

These mistakes have already appeared in the project and must not be reintroduced.

### Dead CSS variables

Avoid:

```tsx
bg-[var(--brand)]
```

or:

```tsx
focus: ring - --brand;
```

Use the Tailwind semantic utility:

```tsx
bg - brand;
```

---

### Hardcoded Tailwind colors

Avoid:

```tsx
bg - white;
bg - red - 600;
text - gray - 500;
border - gray - 300;
```

Use:

```tsx
bg - surface;
bg - danger;
text - text - secondary;
border - border;
```

---

### Missing control colors

Avoid controls that rely entirely on inherited browser colors.

Prefer:

```tsx
bg-surface text-text-primary
```

for interactive controls.

---

### Arbitrary z-index

Avoid:

```tsx
z - 50;
z - 30;
```

Use the centralized z-index scale.

---

### Broken arbitrary-value syntax

Be careful with arbitrary Tailwind syntax.

For example, malformed expressions such as:

```text
bg-(--brand)]
```

may silently result in no styling.

When a token appears not to work, inspect the generated class syntax before assuming the design token is missing.

---

### Using opacity to hide a missing semantic token

Do not create increasingly clever opacity combinations to represent different semantic states.

If two states genuinely have different meanings, give them distinct semantic tokens.

Opacity is acceptable for a simple, low-stakes visual variation where creating another token would add unnecessary complexity.

---

# 18. Before Adding Anything New

Follow this sequence.

### Step 1 — Check the existing design system

Look in:

```text
src/styles/tokens.css
src/styles/theme.css
```

before creating anything new.

---

### Step 2 — Check existing UI primitives

Look in:

```text
src/components/ui
src/components/layout
```

before creating a new component.

---

### Step 3 — Add missing design values correctly

If a required value does not exist:

```text
tokens.css
    ↓
primitive value

theme.css
    ↓
semantic meaning

@theme
    ↓
Tailwind utility

component
    ↓
semantic utility
```

Never skip a layer.

---

### Step 4 — Prefer reuse over duplication

If several features need the same visual behavior, extract it into a reusable UI primitive or semantic token.

Do not copy the same styling into multiple feature components.

---

# 19. AI Coding Tool Rules

If an AI coding tool is modifying this repository, it must follow the same architecture.

The AI must:

- inspect existing tokens before creating new ones
- inspect existing components before creating new ones
- reuse semantic tokens
- avoid raw hex values in UI code
- avoid primitive color ramps in components
- avoid hardcoded Tailwind colors
- avoid arbitrary z-index values
- avoid component-level dark-mode workarounds
- preserve the shadcn compatibility bridge
- avoid introducing a second styling convention

If the correct architectural location is unclear:

> **Ask before inventing a new pattern.**

Do not silently introduce a competing design-system convention.

---

# 20. The Core Mental Model

Always think about styling in this direction:

```text
RAW VALUE
    ↓
SEMANTIC MEANING
    ↓
TAILWIND UTILITY
    ↓
COMPONENT
    ↓
FEATURE
```

For example:

```text
--success-600
      ↓
--theme-match
      ↓
text-match
      ↓
Badge
      ↓
RIE result
```

The RIE feature does not care that match is green.

It only cares that the result is a **match**.

That is the purpose of the design system.

---

# 21. Final Rule

When writing UI code, ask:

> **"Am I expressing what this element means, or am I manually specifying how it looks?"**

Prefer meaning.

```tsx
bg - surface;
text - text - primary;
border - border;
text - match;
bg - processing - tint;
rounded - md;
shadow - sm;
```

Avoid implementation details:

```tsx
bg-white
text-gray-900
border-gray-200
text-green-600
bg-blue-50
rounded-[7px]
shadow-[...]
```

The application should have **one visual language controlled centrally**, not hundreds of independently styled components.

**One design system. One semantic vocabulary. One source of truth.**
