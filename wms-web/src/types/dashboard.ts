export interface MetricItem {
  key: string
  name: string
  value: number
  unit: string
  changeRate: number
  trend: 'up' | 'down' | 'flat'
  icon: string
  color: string
}

export interface TaskItem {
  id: string
  type: 'inbound' | 'outbound' | 'return' | 'scrap' | 'transfer' | 'approval'
  title: string
  description: string
  priority: 'high' | 'medium' | 'low'
  createTime: string
  actionUrl: string
}

export interface AlertItem {
  id: string
  type: 'stock_low' | 'overdue' | 'expire' | 'approval_timeout'
  title: string
  content: string
  level: 'warning' | 'danger' | 'info'
  createTime: string
}

export interface ChartData {
  trend: { date: string; quantity: number; amount: number }[]
  compare: { date: string; inbound: number; outbound: number }[]
  distribution: { name: string; value: number }[]
}

export interface QuickActionItem {
  id: string
  name: string
  icon: string
  url: string
  color?: string
}

export interface DashboardData {
  metrics: MetricItem[]
  tasks: TaskItem[]
  alerts: AlertItem[]
  charts: ChartData
  quickActions: QuickActionItem[]
}

export interface DashboardConfig {
  metrics: string[]
  quickActions: string[]
  chartTimeRange: 'week' | 'month' | 'quarter' | 'year'
  layoutRatio: [number, number]
  theme: string
}