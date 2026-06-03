interface StatCardProps {
  label: string
  value: string
  hint?: string
}

export function StatCard({ label, value, hint }: StatCardProps) {
  return (
    <div className="rounded-lg border border-orange-100 bg-white p-5 shadow-sm">
      <p className="text-sm text-stone-500">{label}</p>
      <p className="mt-2 text-2xl font-bold text-stone-950">{value}</p>
      {hint ? <p className="mt-2 text-xs text-stone-400">{hint}</p> : null}
    </div>
  )
}
