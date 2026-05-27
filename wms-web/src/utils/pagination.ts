/**
 * Converts backend page totals to finite numbers so Element Plus pagination
 * always receives a valid numeric total.
 */
export function normalizePageTotal(total: unknown): number {
  const normalized = Number(total)
  return Number.isFinite(normalized) ? normalized : 0
}
