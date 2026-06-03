interface SectionTitleProps {
  title: string
  extra?: string
}

export function SectionTitle({ title, extra }: SectionTitleProps) {
  return (
    <div className="mb-5 flex flex-wrap items-end justify-between gap-3">
      <h2 className="text-2xl font-bold text-stone-950">{title}</h2>
      {extra ? <p className="text-sm text-stone-500">{extra}</p> : null}
    </div>
  )
}
